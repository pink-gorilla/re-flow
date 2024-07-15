(ns re-flow.core
  (:require
   [taoensso.timbre :refer [info error]]
   [missionary.core :as m]
   [re-frame.core :as rf]
   [modular.ws.core :refer [send!]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]))

(defonce subscriptions (atom {}))

(defmethod -event-msg-handler :re-flow/data
  [{:as ev-msg :keys [?data]}]
  (info "re-flow/data: " ?data)
  (if (vector? ?data)
    (let [[msg-type {:keys [topic data]}] ?data
          ! (get @subscriptions topic)]
      (if ! 
       (do (info "re-flowing topic:" topic data)  
           (! data)) 
        (error "not reflowing. no ! for topic: " topic)))
    (error "data no vector: " ?data)))

(rf/reg-event-db
 :re-flow/data
 (fn [db [_ evt]]
     (let [{:keys [topic data]} evt
           ! (get @subscriptions topic)]
        (info  "re-flow/data: topic: " topic " data: " data)  
        (if !
          (do (info "re-flowing topic:" topic data)
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
         (send! [:re-flow/stop topic]))
       ))))



