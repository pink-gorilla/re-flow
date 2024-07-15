(ns re-flow.core
  (:require
   [taoensso.timbre :refer [debug info error]]
   [missionary.core :as m]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [modular.ws.core :refer [send!]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [re-flow.ui :refer [show-data]]))

(defonce subscriptions (atom {}))

(defmethod -event-msg-handler :re-flow/data
  [{:as ev-msg :keys [?data]}]
  (info "re-flow/data: " ?data)
  (if (vector? ?data)
    (let [[msg-type {:keys [topic data]}] ?data
          ! (get @subscriptions topic)]
      (if !
        (do (debug "re-flowing topic:" topic data)
            (! data))
        (error "not reflowing. no ! for topic: " topic)))
    (error "data no vector: " ?data)))

(rf/reg-event-db
 :re-flow/data
 (fn [db [_ evt]]
   (let [{:keys [topic data]} evt
         ! (get @subscriptions topic)]
        ;(info  "re-flow/data: topic: " topic " data: " data)  
     (if !
       (do (debug "re-flowing topic:" topic data)
           (! data))
       (error "not reflowing. no ! for topic: " topic)))
   db))


(defn re-flow [clj & args]
  (m/observe
   (fn [!]
     (let [topic {:fun clj
                  :args args}]
       (info "re-flow/start " topic)
       (swap! subscriptions assoc topic !)
       (send! [:re-flow/start topic])
       (fn []
         (info "re-flow/stop " topic)
         (swap! subscriptions dissoc topic)
         (send! [:re-flow/stop topic]))))))


(defn flow-ui [{:keys [clj
                       args
                       render]}]
  (let [a (r/atom nil)
        flow (apply re-flow clj args)
        task (m/reduce (fn [_ v]
                         ;(println "processing value: " v)
                         (reset! a v))
                       nil
                       flow)
        _ (println "flow-ui starting..")
        dispose! (task
                  #(println "success: " %)
                  #(println "crash: " %))]
    (fn [{:keys [clj
                 args
                 render]}]
      [show-data render @a])))
