(ns cartao.logic
  (:import [java.time LocalDateTime]))

(defn valor-por-compra
  [compra]
  (get compra :valor 0))


(defn valor-por-categoria
  [[categoria compras]]

  {:categoria categoria
   :valor (->> compras
               (map valor-por-compra)
               (reduce +))})

;soma gastos por categoria
(defn gasto-total-por-categoria
  [compras-por-categoria]
  (->> compras-por-categoria
       (map valor-por-categoria)))

;separa compras por categoria
(defn compras-por-categoria
  [compras]
  (->> compras
       (map :compra)
       (group-by :categoria)))

(defn compras-por-estabelecimento
  [compras nome-estabelecimento]
  (->> compras
       (map :compra)
       (filter #(= (:estabelecimento %) nome-estabelecimento))))

(defn mes-da-data
  [data]
  (.getMonth (LocalDateTime/parse data)))

(defn compras-mensais
  [compras mes]
  (->> compras
       (map :compra)
       (filter #(= (mes-da-data (:data %)) mes))))

(defn compras-por-cliente
  [compras cpf]
  (->> compras
       (filter #(= (get-in % [:cliente :cpf]) cpf))))

(defn total-mensal
  [compras]
  (->> compras
       (map valor-por-compra)
       (reduce +)))