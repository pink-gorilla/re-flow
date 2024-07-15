(ns re-flow.ui
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [webly.spa.resolve :refer [get-resolver]]))

(defn log [& args]
  (.log js/console (apply str args)))

(defn loading-ui [s]
  [:p "loading: " (pr-str s)])

(defn load-error-ui [s]
  [:p "render load error: " (pr-str s)])

(defn create-loader-ui [load-atom s]
  (fn [& args]
    (fn [& args]
      (let [{:keys [status fun]} @load-atom]
        (case status
          :loading [loading-ui s]
          :error [load-error-ui s]
          :loaded (into [fun] args)
          [loading-ui s])))))

(defn update-atom [load-a p]
  (-> p
      (.then (fn [f]
               (log "get-render-fn resolved fun: " f)
               (if f
                 (swap! load-a assoc :status :loaded :fun f)
                 (swap! load-a assoc :status :error))))
      (.catch (fn [d]
                (log "get-render-fn: require result failure!")
                (swap! load-a assoc :status :error)))))

(defn get-render-fn [s]
  (let [load-a (r/atom {:status :loading})
        resolve-fn (get-resolver)
        fun (resolve-fn s)]
    (if (p/promise? fun)
      (update-atom load-a fun)
      (swap! load-a assoc :status :loaded :fun fun))
    (create-loader-ui load-a s)))


(defn show-data [s v]
  (fn [s v]
    (log "show-data symbol: " s " val: " v)
    (let [render-fn (get-render-fn s)]
      (if render-fn
        [render-fn v]
        [:p "unknown render-fn: " (str s)]))))



