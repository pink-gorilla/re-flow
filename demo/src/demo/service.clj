(ns demo.service
  (:require
   [missionary.core :as m]))


(defn a-seed []
  (m/seed [1 2 3 4 5]))


(defn another-seed [n]
  (m/seed (range n)))


(defn quotes []
  ;(m/stream
   (m/ap
    (loop [p (rand-int 1000)]
      (m/amb
       (m/? (m/sleep 500 p))
       (recur (rand-int 10000))))))
;)
