(ns demo.service
  (:require 
     [missionary.core :as m]
   )
  )


(defn a-seed []
   (m/seed [1 2 3 4 5]))


(defn another-seed [n]
  (m/seed (range n)))






