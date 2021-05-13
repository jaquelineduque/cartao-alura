(ns cartao.logic)

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
       ;(filter #(> (:preco-total %) 500) resumo))
       (filter #(= (:estabelecimento %) nome-estabelecimento))))