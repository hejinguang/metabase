(ns metabase.api.label-test
  "Tests for `/api/label` endpoints."
  (:require [expectations :refer [expect]]
            [metabase.db :refer [cascade-delete]]
            [metabase.models.label :refer [Label]]
            [metabase.test.data.users :refer [user->client]]
            [metabase.test.util :refer [expect-with-temp]]
            [metabase.util :as u]))

;;; GET /api/label -- list all labels
(expect-with-temp [Label [{label-1-id :id} {:name "Toucan-Approved"}]
                   Label [{label-2-id :id} {:name "non-Toucan-Approved"}]]
  [{:id label-2-id, :name "non-Toucan-Approved", :slug "non_toucan_approved", :icon nil}  ; should be sorted by name, case-insensitive
   {:id label-1-id, :name "Toucan-Approved",     :slug "toucan_approved",     :icon nil}]
  ((user->client :rasta) :get 200, "label"))

;;; POST /api/label -- check that we can create a new label
(expect
  {:name "Warning: May Contain Toucans!!", :slug "warning__may_contain_toucans__", :icon nil}
  (u/prog1 (dissoc ((user->client :rasta) :post 200 "label", {:name "Warning: May Contain Toucans!!"})
                   :id)
    (cascade-delete Label :slug (:slug <>))))

;;; PUT /api/label/:id -- update a label. Make sure new slug is generated
(expect-with-temp [Label [{label-id :id} {:name "Toucan-Approved"}]]
  {:id label-id, :name "Bird-Friendly", :slug "bird_friendly", :icon nil}
  ((user->client :rasta) :put 200, (str "label/" label-id) {:name "Bird-Friendly"}))

;;; DELETE /api/label/:id -- delete a label
(expect-with-temp [Label [{label-id :id} {:name "This will make the toucan very cross!"}]]
  nil
  (do ((user->client :rasta) :delete 204, (str "label/" label-id))
      (Label label-id)))
