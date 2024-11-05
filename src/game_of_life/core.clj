(ns game-of-life.core)

;; grid size
(def width 20)
(def height 20)

;; create a random grid
(defn make-grid []
  (vec (for [_ (range height)]
         (vec (for [_ (range width)]
                (rand-nth [0 1]))))))

;; count live neighbors of a cell
(defn count-live-neighbors [grid x y]
  (let [neighbors [[-1 -1] [-1 0] [-1 1]
                   [0 -1]        [0 1]
                   [1 -1] [1 0] [1 1]]]
    (reduce + (for [[dx dy] neighbors
                    :let [nx (+ x dx) ny (+ y dy)]
                    :when (and (>= nx 0) (< nx height)
                               (>= ny 0) (< ny width))]
                (get-in grid [nx ny] 0)))))

;; apply rules (if alive and 2 or 3 neighbors, stays alive, else die)
(defn update-grid [grid]
  (vec (for [x (range height)]
         (vec (for [y (range width)]
                (let [live-neighbors (count-live-neighbors grid x y)
                      cell (get-in grid [x y])]
                  (cond
                    (and (= cell 1) (or (= live-neighbors 2) (= live-neighbors 3))) 1
                    (and (= cell 0) (= live-neighbors 3)) 1
                    :else 0)))))))

;; display grid
(defn display-grid [grid]
  (doseq [row grid]
    (println (apply str (map #(if (= % 1) "*" ".") row))))
  (println))

;; game loop
(defn -main []
  (loop [grid (make-grid)]
  
    (display-grid grid)

    ;; pause to see
    (Thread/sleep 500) 
    
    ;; clear screen
    (println "\033[2J")

    ;; apply rules
    (recur (update-grid grid))))

(-main)
