; imports
(ns game-of-life.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;; grid size
(def width 100)
(def height 100)
(def cell-size 10) ;; Size of each cell in pixels

;; create a random grid
(defn make-grid []
  (vec (for [_ (range height)]
         (vec (for [_ (range width)]
                (rand-nth [0 1]))))))

;; live neighbors of a cell
(defn count-live-neighbors [grid x y]
  (let [neighbors [[-1 -1] [-1 0] [-1 1]
                   [0 -1]        [0 1]
                   [1 -1] [1 0] [1 1]]]
    (reduce + (for [[dx dy] neighbors
                    :let [nx (+ x dx) ny (+ y dy)]
                    :when (and (>= nx 0) (< nx height)
                               (>= ny 0) (< ny width))]
                (get-in grid [nx ny] 0)))))

;; apply the rules 
(defn update-grid [grid]
  (vec (for [x (range height)]
         (vec (for [y (range width)]
                (let [live-neighbors (count-live-neighbors grid x y)
                      cell (get-in grid [x y])]
                  (cond
                    (and (= cell 1) (or (= live-neighbors 2) (= live-neighbors 3))) 1
                    (and (= cell 0) (= live-neighbors 3)) 1
                    :else 0)))))))

;; Draw the grid w/ Quil
(defn draw-grid [grid]
  (q/background 255) ;; White background
  (doseq [x (range height)
          y (range width)]
    (when (= 1 (get-in grid [x y]))
      (q/fill 0) ;; Black color for live cells
      (q/rect (* y cell-size) (* x cell-size) cell-size cell-size))))

;; Setup Quil
(defn setup []
  (q/frame-rate 10) ;; Frames per second
  (q/color-mode :rgb)
  {:grid (make-grid)}) ;; Init state

;; Update Quil
(defn update-state [state]
  (update state :grid update-grid)) ;; <-- Update grid
 
;; Draw Quil
(defn draw-state [state]
  (draw-grid (:grid state)))

;; Main function to run the sketch
(defn -main []

  ;; Run sketch
  (q/defsketch game-of-life
    :title "Conway's Game of Life"

    ;; Set the size of the window
    :size [(* width cell-size) (* height cell-size)]

    ;; setup, update, and draw functions
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

;; Entry point
(-main)
