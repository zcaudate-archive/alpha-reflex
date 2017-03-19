(ns alpha-reflex.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [alpha-reflex.events]
            [alpha-reflex.subs]))

(def React (js/require "react-native"))
(def DIM
  (-> (js/require "Dimensions")
      (.get "window")))

(def SIZE 4)
(def CELL-SIZE (.floor js/Math (* 0.2 (.-width DIM))))
(def CELL-PADDING (.floor js/Math (* 0.05 CELL-SIZE)))
(def BORDER-RADIUS (* 2 CELL-PADDING))
(def TILE-SIZE (- CELL-SIZE (* 2 CELL-PADDING)))
(def LETTER-SIZE (.floor js/Math (* 0.75 TILE-SIZE)))

(def StyleSheet (.-StyleSheet React))

(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity React)))
(def text (r/adapt-react-class (.-Text React)))
(def view (r/adapt-react-class (.-View React)))
(def app-registry (.-AppRegistry React))

(def styles
  {:container {:flex 1
               :justifyContent "center"
               :alignItems "center"
               :backgroundColor "#644B62"}
   :board {:width (* CELL-SIZE SIZE)
           :height (* CELL-SIZE SIZE)
           :backgroundColor "transparent"}
   :tile {:position "absolute"
          :width TILE-SIZE
          :height TILE-SIZE
          :borderRadius BORDER-RADIUS
          :justifyContent "center"
          :alignItems "center"
          :backgroundColor "#BEE1D2"}
   :letter {:color "#333"
            :fontSize LETTER-SIZE
            :fontFamily "NukamisoLite" 
            :backgroundColor "transparent"}})

(defn position [col row]
  {:left (+ (* col CELL-SIZE) CELL-PADDING)
   :top  (+ (* row CELL-SIZE) CELL-PADDING)})

(defn board-view []
  (vec (concat [view {:style (:board styles)}]
               (for [row (range SIZE)
                     col (range SIZE)]
                 (let [key (+ col (* row SIZE))
                       letter (.fromCharCode js/String (+ 65 key))
                       pos  (position col row)]
                   [view {:key key :style (merge (:tile styles) pos)}
                    [text {:style (:letter styles)} letter]])))))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style (:container styles)}
       (board-view)])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "AlphaReflex" #(r/reactify-component app-root)))
