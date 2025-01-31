(ns athens.common-events.atomic-ops.block-save-test
  (:require
    [athens.common-db                     :as common-db]
    [athens.common-events.fixture         :as fixture]
    [athens.common-events.graph.ops       :as graph-ops]
    [athens.common-events.resolver.atomic :as atomic-resolver]
    [clojure.test                         :as t]
    [datascript.core                      :as d]))


(t/use-fixtures :each fixture/integration-test-fixture)


(t/deftest block-save-atomic-op-building-tests

  (t/testing "Simple `:block/save` cases, just saving string."
    (let [block-uid     "block-uid-1"
          new-str       "new-string"
          block-save-op (graph-ops/build-block-save-op @@fixture/connection
                                                       block-uid
                                                       new-str)]
      (t/is (= #:op{:type    :block/save,
                    :atomic? true,
                    :args    {:block/uid    block-uid
                              :block/string new-str}}
               block-save-op))))

  (t/testing "Requires `:page/new`, getting interesting"
    (let [block-uid         "block-uid-2"
          new-str           "[[new-page]]"
          {:op/keys [consequences
                     atomic?
                     trigger
                     type]} (graph-ops/build-block-save-op @@fixture/connection
                                                           block-uid
                                                           new-str)]

      (t/is (= :composite/consequence type))
      (t/is (false? atomic?))
      (t/is (= :block/save (:op/type trigger)))
      (t/is (= 2 (count consequences)))
      (t/is (= :page/new (-> consequences first :op/type)))
      (t/is (= #:op{:type    :block/save,
                    :atomic? true,
                    :args
                    {:block/uid    "block-uid-2",
                     :block/string "[[new-page]]"}}
               (-> consequences second))))))


(t/deftest block-save-to-tx-resolution-tests

  (t/testing "Simple `:block/save` case, just saving string, go."
    (let [page-1-uid  "page-1-uid"
          child-1-uid "child-1-1-uid"
          empty-str   ""
          new-str     "a-o-k"
          setup-txs   [{:block/uid      page-1-uid
                        :node/title     "test page 1"
                        :block/children {:block/uid      child-1-uid
                                         :block/string   empty-str
                                         :block/order    0
                                         :block/children []}}]]
      (d/transact! @fixture/connection setup-txs)
      (let [child-1-eid    (common-db/e-by-av @@fixture/connection
                                              :block/uid child-1-uid)
            block-save-op  (graph-ops/build-block-save-op @@fixture/connection child-1-uid new-str)
            block-save-txs (atomic-resolver/resolve-atomic-op-to-tx @@fixture/connection
                                                                    block-save-op)]
        (t/is (= empty-str (common-db/v-by-ea @@fixture/connection
                                              child-1-eid :block/string)))
        (d/transact! @fixture/connection block-save-txs)
        (t/is (= new-str (common-db/v-by-ea @@fixture/connection
                                            child-1-eid :block/string))))))

  (t/testing "Complex case `:block/save` case, just saving string, go."
    (let [page-1-uid  "page-1-uid"
          child-1-uid "child-1-1-uid"
          empty-str   ""
          page-title  "linked page"
          new-str     (str "[[" page-title "]]")
          setup-txs   [{:block/uid      page-1-uid
                        :node/title     "test page 1"
                        :block/children {:block/uid      child-1-uid
                                         :block/string   empty-str
                                         :block/order    0
                                         :block/children []}}]]
      (d/transact! @fixture/connection setup-txs)
      (let [child-1-eid        (common-db/e-by-av @@fixture/connection
                                                  :block/uid child-1-uid)
            block-save-op      (graph-ops/build-block-save-op @@fixture/connection child-1-uid new-str)
            block-save-atomics (graph-ops/extract-atomics block-save-op)]
        (t/is (nil? (common-db/e-by-av @@fixture/connection
                                       :node/title page-title)))
        (t/is (= empty-str (common-db/v-by-ea @@fixture/connection
                                              child-1-eid :block/string)))
        (doseq [atomic-op block-save-atomics
                :let      [atomic-txs (atomic-resolver/resolve-atomic-op-to-tx @@fixture/connection atomic-op)]]
          (d/transact! @fixture/connection atomic-txs))
        (t/is (not (nil? (common-db/e-by-av @@fixture/connection
                                            :node/title page-title))))
        (t/is (= new-str (common-db/v-by-ea @@fixture/connection
                                            child-1-eid :block/string)))))))

