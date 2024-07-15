(ns demo.page
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [missionary.core :as m]
   [re-flow.core :refer [re-flow]]))

(defn a-flow-demo []
   (let [flow (re-flow 'demo.service/a-seed)
         task (m/reduce (fn [_ v]
                          (println "processing value: " v))
                        nil
                        flow)
          _ (println "a-flow-demo starting..")
          dispose! (task
                     #(println "success: " %)
                     #(println "crash: " %))]
    nil))

(defn show-page []
  [:div
   [:p.text-big.text-blue-900.text-bold "re-flow  demo .."]

   [:div.bg-green-500.m-5.p-5
    [:button.bg-blue-500 {:on-click #(a-flow-demo)} "a-flow"]
   ]
   ])

(defn page [_route]
  [show-page])

