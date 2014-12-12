(ns pucks.operators
 (:use [clojush.pushstate]
       [clojush.util])
  )

(defn ps
  "Protected squart root; returns 0 if the radicand is less than zero."
  [radicand]
  (if (> 0 radicand)
    radicand
    (java.lang.Math/sqrt radicand)))

(defn pdiv
  [num denom]
  (if (= 0 denom)
    0
    (/ num denom)))

(define-registered
  float_sqrt
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (keep-number-reasonable 
                   (ps (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))

;(define-registered
;  float_log
;  (fn [state]
;    (if (not (empty? (:float state)))
;      (push-item (keep-number-reasonable 
;                   (plog (stack-ref :float 0 state)))
;                 :float
;                 (pop-item :float state))
;      state)))

(define-registered
  float_ex
  (fn [state]
    (if (not (empty? (:float state)))
      (push-item (keep-number-reasonable 
                   (Math/pow Math/E (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))

(define-registered
  float_cbrt
  (fn [state]
    (if (not(empty? (:float state)))
      (push-item (keep-number-reasonable
                   (Math/cbrt (stack-ref :float 0 state)))
                 :float
                 (pop-item :float state))
      state)))