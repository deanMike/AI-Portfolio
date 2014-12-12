;; Definition for user agents. This is a good template to build on to produce
;; smarter agents.

(ns pucks.agents.user
  (:use [pucks globals util vec2D]
        [pucks.agents active]
        [pucks.pushpucks]
        [clojush.pushgp.pushgp]
        [clojush.pushgp.report]))


#_(defn user-proposals [p]
    {:acceleration 1
     :rotation (direction->rotation (:velocity p))})

(defn user-proposals [p]
  (let [death (first (filter :zapper (:sensed p))) 
        life (first (filter :vent (:sensed p))) 
        obstacle (first (filter :stone (:sensed p)))
        opener (first (filter :chest (:sensed p)))
        door (first (filter :gate (:sensed p)))]
    
    {:acceleration (if (and (< (:energy p) 0.6) (not (empty? (filter :vent (:overlaps p)))))
                     (* 0.25 (- (length (:velocity p))))
                     (if (not (empty? (filter :zapper (:sensed p))))
                       0.5
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
     
     ;    :thrust-angle (if (not (empty? (filter :zapper (:sensed p))))
     ;                    (* (- 1 0) (direction->rotation (:position death))))
     ;                    (if (not (empty? (filter :stone (:overlaps p))))
     ;                      ;(+ half-pi (direction->rotation (:position obstacle)))
     ;                      (+ half-pi (direction->rotation (apply avgv (map :position (filter :stone (:overlaps p))))))
     ;                      0))
     :memory (if (and (not (empty? (filter :chest (:sensed p)))) (empty? (:memory p)))
               {:pushprog (pushgp argmap)})
     ;{'id (:id opener)})
     
     :transfer (vec (concat 
                      ;; ask for a key from anyone
                      (for [anyone (:overlaps p)]
                        (if (or (> (:energy p) (:energy anyone)) (not (:mobile anyone)))
                          {:self (:id p)
                           :other (:id anyone)
                           :bid {:request {:inventory :key}}
                           :ask {:inventory :key}}
                          {:self (:id p)
                           :other (:id anyone)
                           }
                          ))
                      ;; offer a key to a gate that promises to open for a positive amount of time
                      (for [recipient (filter :mobile (:overlaps p))
                            item (:inventory p)]
                        {:self (:id p)
                         :other (:id recipient)
                         :bid {:inventory item}
                         :ask {:request {:inventory item}}})
                      (for [gate (filter :gate (:overlaps p))]
                        {:self (:id p)
                         :other (:id gate)
                         :bid {:inventory :key}
                         :ask (fn [my-bid your-bid]
                                (when-let [open-promise (:open (:promise your-bid))]
                                  (pos? open-promise)))})))
     }))



(defn user []
  (merge (active)
         {:user true
          :proposal-function user-proposals}))