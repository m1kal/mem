(ns mem.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(defonce values (atom {:values [[1 2 3] [2 3 4] [2 2 3] [3 4 1]]
  :active [[1 1]] :clicks 0}))

(defn restart! []
  (swap! values assoc :values
    (vec (map vec (partition 3 (shuffle (flatten (repeat 2 [1 2 3 3 4 4]))))))
    :clicks 0
  )
  )

(defn two-uncovered []
  (if (= (count (set
     (map #(get-in @values [:values (first %) (last %)]) (:active @values))
     ))  1)
    (doseq [xy (:active @values)]
     (swap! values assoc-in [:values (first xy) (last xy)] nil)
     ))
  (swap! values assoc :active [])
)

(defn clicked [n m]
  (if (= (count (:active @values)) 2)
    (two-uncovered)
    (do
      (swap! values update :clicks inc)
    (if (some #{[n m]} (:active @values))
      (swap! values assoc :active (remove #(= [n m] %) (:active @values) ))
      (swap! values assoc :active (conj (:active @values) [n m]))
    )
))
 (if (empty? (filter identity (flatten (:values @values))))
  (do
  (swap! values assoc :win true)
  (restart!)
  )
  (swap! values assoc :win false)
 )
  )

(defn card [n m]
  [:g
   [:rect {:x (+ 5 (* 100 n)) :y (+ 5 (* 100 m)) :width 90 :height 90 :fill "red"
  :on-click (fn [e]
      (clicked n m)
     )
  }]
   [:text {:x (+ 40 (* 100 n)) :y (+ 40 (* 100 m)) :fill "blue" :font-size 20}
   (let [active (:active @values) is-active (some #{[n m]} active)]
    (if is-active (str (((:values @values) n) m)) "x")
   )

  ]]

)

(defn hello-world []
  [:div
   [:div
    [:button
    {:on-click restart! }
    "restart"]
   ]

  [:svg {:width 450 :height 350}
   [:line {:x1 0 :y1 100 :x2 400 :y2 100 :stroke "black"}]
   [:line {:x1 0 :y1 200 :x2 400 :y2 200 :stroke "black"}]
   [:line {:x1 100 :y1 0 :x2 100 :y2 300 :stroke "black"}]
   [:line {:x1 200 :y1 0 :x2 200 :y2 300 :stroke "black"}]
   [:line {:x1 300 :y1 0 :x2 300 :y2 300 :stroke "black"}]
   (apply concat (map (fn [x]
     (map (fn [y] [card x y]) (range 3))
     ) (range 4)))
  ]
  [:div (:clicks @values) " clicks"]
  [:div (if (:win @values) [:h1 {:style {:color "magenta"}} "Victory!"] )]

   ])


(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
