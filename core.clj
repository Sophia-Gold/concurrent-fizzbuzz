(ns fizzbuzz.core
  (:require [clojure.core.async
             :as async
             :refer :all]))

;transducer:
(defn trans-fizz []
  (fn [xf]
    (fn 
      ([] (xf))
      ([result] (xf result))
      ([result input]
       (condp #(zero? (mod %2 %1)) input
         15 (xf result "fizzbuzz")
         3 (xf result "fizz")
         5 (xf result "buzz")
         (xf result input))))))

;~.075ms on 1.7 GHz Intel Core i5
;; (time (transduce (trans-fizz) conj (range 1 100)

;; ;~2.2ms
;; (defn loop-fizz []
;;   (let [ints (drop 1 (range))
;;         out (chan 1 (trans-fizz))]
;;     (onto-chan out ints)
;;     out))

;~3.3ms
(defn loop-fizz []
  (let [ints (drop 1 (range))
        out (chan 1 (trans-fizz))]
    (go-loop [val ints]
      (when (>! out (first val))
        (recur (rest val))))
    out))

(defn consume [n ch]
  (dorun (repeatedly n #(<!! ch))))

(defn print-consume [n ch]
  (dorun (print (repeatedly n #(<!! ch)))))

(defn -main []
  (do (print-consume 100 (loop-fizz))
      (time (consume 100 (loop-fizz)))))  
