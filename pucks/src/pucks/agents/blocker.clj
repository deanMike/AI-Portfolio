;;blockers have the same proposal functions as users but with the ability to drop flags near good things.

(ns pucks.agents.blocker
  (:use [pucks globals util vec2D]
        [pucks.agents active]))


 #_(defn blocker-proposals [p]
   {:acceleration 1
    :rotation (direction->rotation (:velocity p))})

 (defn blocker-proposals [p]
   (let [death (first (filter :zapper (:sensed p))) 
         life (first (filter :vent (:sensed p))) 
         obstacle (first (filter :stone (:sensed p)))
         opener (first (filter :chest (:sensed p)))
         door (first (filter :gate (:sensed p)))]
     
   {:acceleration (if (and (< (:energy p) 0.6) (not (empty? (filter :vent (:overlaps p)))))
                    (* 0.25 (- (length (:velocity p))))
                    (if (or (not (empty? (filter :zapper (:sensed p)))) (not (empty? (filter :stone (:overlaps p)))))
                      (* 0.5 (- (length (:velocity p))))
                      1))
     :rotation (if (and (not (empty? (:inventory p))) (not (empty? (filter :gate (:sensed p)))))
                 (direction->rotation (:position door))
                 (if (not (empty? (filter :zapper (:sensed p))))
                   (+ pi (direction->rotation (:position death)))
                   (if (not (empty? (filter :stone (:overlaps p))))
                     ;(+ half-pi (direction->rotation (:position obstacle)))
                     (+ half-pi (direction->rotation (apply avgv (map :position (filter :stone (:overlaps p))))))
                     (if (and (< (:energy p) 0.6) (not (empty? (filter :vent (:sensed p)))))
                       (direction->rotation (:position life))
                       (if (and (empty? (:inventory p)) (not (empty? (concat (filter :chest (:sensed p))))))
                         ;(if (not (empty? (:inventory (first (:sensed p)))))
                         ;(if (= 0 (:total-error (:program (pushgp argmap))))
                         (direction->rotation (:position opener))
                         (if (= 0 (mod 50 (:steps p)))
                           (+ 0.1 (direction->rotation (:velocity p)))
                           (direction->rotation (:velocity p))))))));)
                                  
    :thrust-angle (if (and (not (empty? (:inventory p))) (not (empty? (filter :gate (:sensed p)))))
                    (direction->rotation (:position door))
                    (if (not (empty? (filter :zapper (:sensed p))))
                      (* (- 0 1) (direction->rotation (:position death)))
                      (if (not (empty? (filter :stone (:sensed p))))
                        (- (direction->rotation (:position obstacle)) 1.0)
                         0)))
    :memory (if (not (empty? (filter :zapper (:sensed p))))
              (if (not (empty? (filter (:id death) (:memory p))))
                [(:id death) (:position death)]
                nil)
              (if (not (empty? (filter :vent (:sensed p))))
                (if (empty? (filter (:id life) (:memory p)))
                 ;(hash-map (:position life) (:position p))
                 (hash-map :vent-pos (map + (:position life) (:position p)), :vent-id (:id life), :rel-position (:position p))
                 nil)))
    ;;Spawns a flag only near a vent, a gate, or a chest.
    :spawn  (if (empty? (filter :stone (:overlaps p)))
              (if (not (empty? (filter :zapper (:sensed p))))
                [(assoc ((:spawn-function p))
                      :position [0 0])]))
    
    }))
 
 

(defn blocker [spawn-function]
  (merge (active)
         {:blocker true
          :color [100 0 100]
         :proposal-function blocker-proposals
         :spawn-function spawn-function
         }))
