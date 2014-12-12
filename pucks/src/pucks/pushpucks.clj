(ns pucks.pushpucks
  (:use [clojush.pushgp.pushgp]
        [clojush.random]
        [clojush pushstate interpreter]
        clojush.instructions.common))

(defn random-math [x] 
  ((rand-nth [+ - *]) x (rand-int 10)))

(def argmap
  {:use-single-thread true
   :error-function (fn [program]
                     (doall
                       (for [input (range 3)]
                         (let [state (run-push program
                                               (push-item input :input
                                                          (push-item input :integer
                                                                     (make-push-state))))
                               top-bool (top-item :boolean state)]
                           (if (not (= top-bool :no-stack-item))
                             (if (= top-bool (odd? input)) 0 1)
                             1000)))))
   :atom-generators (concat (registered-nonrandom)
                            (list (fn [] (lrand-int 100))
                                  'in1
                                  'code_rand))
   })