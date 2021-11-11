(ns athens.common-events.graph.atomic
  "⚛️ Atomic Graph Ops.

  3 groups of Graph Ops:
  * block
  * page
  * shortcut")


;; Block Ops

(defn make-block-new-op
  "Creates `:block/new` atomic op.
   - `block-uid` - `:block/uid` of new block to be created
   - `position` - new blocks position
      - for siblings: `:before` or `:after` together with `ref-uid`
      - for children: `:first`, `:last` together with `ref-uid` or `ref-name`"
  [block-uid position]
  {:op/type    :block/new
   :op/atomic? true
   :op/args    {:block-uid block-uid
                :position  position}})


(defn make-block-save-op
  "Creates `:block/save` atomic op.
   - `block-uid` - `:block/uid` of block to be saved
   - `string` - new value of `:block/string` to be saved"
  [block-uid new-string]
  {:op/type    :block/save
   :op/atomic? true
   :op/args    {:block-uid block-uid
                :string    new-string}})


(defn make-block-open-op
  "Creates `:block/open` atomic op.
   - `block-uid` - `:block/uid` of block to be opened/closed
   - `open?` - should we open or close the block"
  [block-uid open?]
  {:op/type    :block/open
   :op/atomic? true
   :op/args    {:block-uid block-uid
                :open?     open?}})


(defn make-block-remove-op
  "Creates `:block/remove` atomic op.
   - `block-uid` - `:block/uid` of block to be removed"
  [block-uid]
  {:op/type    :block/remove
   :op/atomic? true
   :op/args    {:block-uid block-uid}})


(defn make-block-move-op
  "Creates `:block/move` atomic op.
   - `block-uid` - `:block/uid` of block to move
  - `position` - new blocks position
      - for siblings: `:before` or `:after` together with `ref-uid`
      - for children: `:first`, `:last` together with `ref-uid` or `ref-name`"
  [block-uid position]
  {:op/type    :block/move
   :op/atomic? true
   :op/args    {:block-uid block-uid
                :position  position}})


;; Page Ops

(defn make-page-new-op
  "Creates `:page/new` atomic op.
   - `name` - Page name to be created "
  [name]
  {:op/type    :page/new
   :op/atomic? true
   :op/args    {:name name}})


(defn make-page-rename-op
  "Creates `:page/rename` atomic op.
   - `old-name` - Page name before rename,
   - `new-name` - Page should have this name after operation"
  [old-name new-name]
  {:op/type    :page/rename
   :op/atomic? true
   :op/args    {:old-name old-name
                :new-name new-name}})


(defn make-page-merge-op
  "Creates `:page/merge` atomic op.
   - `from-name` - name of page to be merged into `to-name`
   - `to-name` - name merge to this page"
  [from-name to-name]
  {:op/type    :page/merge
   :op/atomic? true
   :op/args    {:from-name from-name
                :to-name   to-name}})


(defn make-page-remove-op
  "Creates `:page/remove` atomic op.
   - `name` - name of the page to be deleted"
  [name]
  {:op/type    :page/remove
   :op/atomic? true
   :op/args    {:name name}})


;; Shortcut

(defn make-shortcut-new-op
  "Creates `:shortcut/new` atomic op.
   - `name` - name of page to be added to shortcuts"
  [name]
  {:op/type    :shortcut/new
   :op/atomic? true
   :op/args    {:name name}})


(defn make-shortcut-remove-op
  "Creates `:shortcut/remove` atomic op.
   - `name` - name of page to be removed from shortcuts"
  [name]
  {:op/type    :shortcut/remove
   :op/atomic? true
   :op/args    {:name name}})


(defn make-shortcut-move-op
  "Creates `:shortcut/move` atomic op.
   - `page-uid` - `:block/uid` of page to be moved to new position in shortcuts
   - `index` - new position for `page-uid` shortcut"
  [page-uid index]
  {:op/type    :shortcut/move
   :op/atomic? true
   :op/args    {:page-uid page-uid
                :index    index}})
