;; Definitions for flag agents.

(ns pucks.agents.flag
    (:use quil.core 
        [pucks globals util vec2D]
        pucks.agents.generic))

(defn draw-flag [p]
  (let [[x y] (:position p)
        radius (/ (:radius p) 2)
        [r g b] (:color p)]
    (push-matrix)
    (translate x y)
    ;; membrane
    (fill r g b 32)
    (ellipse 0 0 (* radius 2) (* radius 2))
    ;; core
    (ellipse 0 0 radius radius)
    (fill 0 0 0)
    (text-align :center)
    (text (str (:id p) (:position p)) 0 0))
    (pop-matrix))

(defn flag-proposals [p]
  {})

;; Beacons are defined to be the maximal size compatible with the provided
;; sensor range and neighborhood size:
;; 
;; flag-radius + sensor-range = neighborhood-size
;; => flag-radius = neighborhood-size - sensor-range

(defn flag []
  (merge (generic)
         {:flag true
          :solid false
          :color [0 55 55]
          :radius (- (:neighborhood-size @pucks-settings) 
                     (:sensor-range @pucks-settings))
          :draw-function draw-flag
          :proposal-function flag-proposals
          :id (gensym "flag-")
          :position (rand-xy)}))