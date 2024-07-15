(ns re-flow.core
   (:require
    [taoensso.timbre :refer [info error]]
    [missionary.core :as m]
    [de.otto.nom.core :as nom]
    [modular.ws.core :refer [send!]]
    [modular.ws.msg-handler :refer [-event-msg-handler]]
    [clj-service.executor :refer [execute-with-binding]]
    [modular.permission.session :refer [get-user]]))

 (defn publish!
   [push! flow]
   (let [rf (fn [r v]
              (try 
                 (info "pushing value: " v)  
                (catch Exception ex
                  nil))
              
             (push! v)
              r)]
     (m/reduce rf nil flow)))

 (defonce tasks (atom {}))

 (defn save-task [topic task]
   (swap! tasks assoc topic task))


 (defn send-push! [{:as ev-msg :keys [id ?data ring-req ?reply-fn uid send-fn]}
                   msg-type response]
   (try
     ; :?reply-fn nil
     ; :ch-recv .. core.async channel
     ; :client-id "5f5236dd-a7f9-4372-a3d5-5a4ef255678b"
     ; :connected-uids
     ; :uid "7dd3a962-cb67-4ea6-b887-06280a2e372b"   ... this corresponds to the connected uids.
     ; :send-fn this will send data to the browser.
     ;(info "send-push!: " ev-msg)
     (if (and msg-type response)
       (cond
         ?reply-fn (?reply-fn [msg-type response])
         uid (send-fn uid [msg-type response])
         :else (error "send-push issue: neither ?reply-fn nor uid was set!"))
       (error "send-push issue: msg-type and response have to be set, msg-type:" msg-type "response: " response))
     (catch Exception ex
       (error "send-push exception! ")))
   nil)

 (defn start-clj-flow! [{:keys [clj-service permission-service] :as this}]
   (defmethod -event-msg-handler :re-flow/start
     [{:keys [event _id _?data uid] :as req}]
     (let [[_ topic] event ; _ is :clj/re-flow
           _ (info "clj-flow-start topic: " topic)
           {:keys [fun args]} topic
           _ (info "clj-flow-start get user..")
           user nil ; (get-user permission-service uid)
           _ (info "clj-flow-start get flow..")
           flow (execute-with-binding clj-service user uid fun args)
           _ (info "clj-flow-start " topic " :flow: " (fn? flow))
           _ (info "clj-flow-start " topic " :flow: " flow)
           push! (fn [data]
                   (let [msg {:topic topic :data data}]
                     (send-push! req :re-flow/data msg)))]
       (if (nom/anomaly? flow) ;  (m/flow? flow) would be better
         (do (error "the clj-function has returned an anomaly. not working.")
             (error flow))
         (let [task (publish! push! flow)]
                 ;(push! nil)
               (future
                 (info "starting missionary task..")
                 (let [dispose! (task  #(prn ::success %)
                                       #(prn ::crash %))]
                   (info "saving task..")
                   (save-task topic dispose!)
                   nil))))
       nil))
 
 (defmethod -event-msg-handler :re-flow/stop
   [{:keys [event _id _?data uid] :as req}]
   (let [[_ params] event ; _ is :clj/re-flow
         {:keys [fun args]} params
         user (get-user permission-service uid)
         topic {:clj fun :args args}
         _ (info "clj-flow/stop " topic)
         task (get @tasks topic)]
     (if task
       (do (info "disposing of task " topic)
           (task) ; dispose of the task  
           )
       (error "no task found to dispose: " topic))
     (swap! tasks dissoc topic))))
   