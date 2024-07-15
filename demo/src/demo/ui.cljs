(ns demo.ui)

(defn a-flow-ui [v]
  [:div 
    "current a-flow value: "
    (pr-str v)])


(defn quotes-ui [v]
  (let [s (if v
            (str v)
            "[no value]")]
    [:div {:class (cond 
                   (not v) ""
                   (< v 500) "bg-red-500"
                   :else "bg-green-500")}
       s]))
  
