(ns cartao.core
  (:require [cartao.db :as c.db]
            [cartao.logic :as c.logic]))


(defn todas-as-compras
  []
  (c.db/todas-as-compras))

(defn gasto-por-categoria
  []
  (->
    (c.db/todas-as-compras)
    (c.logic/compras-por-categoria)
    (c.logic/gasto-total-por-categoria)))

(defn compras-por-estabelecimento
  [nome-estabelecimento]
  (->
    (c.db/todas-as-compras)
    (c.logic/compras-por-estabelecimento nome-estabelecimento)))


(println "Todas as compras" (todas-as-compras))
(println "Gasto por categoria: " (gasto-por-categoria))
(println "Compras no teatro de magos " (compras-por-estabelecimento "Teatro de magos"))
