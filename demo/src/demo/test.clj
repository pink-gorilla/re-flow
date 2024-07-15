(ns demo.test
  (:require
   [missionary.core :as m]
   [taoensso.timbre :refer [info error]]))

(defn publish!
  [push! flow]
  (let [rf (fn [r v]
             (info "pushing value: " v)
             ;(push! v)
             r)]
    (m/reduce rf nil flow)))


(def flow (m/seed [1 2 3 4 5]))


(def task (publish! nil flow))

(def dispose! (task  #(prn ::success %)
                     #(prn ::crash %)))


task

(type task)

(class task)

(fn? task)

(info "saving task..")