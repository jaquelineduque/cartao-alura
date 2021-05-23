(ns cartao.core
  (:require [cartao.db :as c.db]
    ;[cartao.logic :as c.logic]
            [cartao.model :as c.model]
            [datomic.api :as d])
  (:import [java.time LocalDateTime])
  (:use [clojure pprint]))

;(c.db/apaga-banco!)
(def conn (c.db/abre-conexao!))
(c.db/cria-schema! conn c.db/schema-compras)

;adiciona clientes no banco
(def gandalf (c.model/novo-cliente "14971065083" "Gandalf the Gray" "gandalf.gray@shire.com"))
(def alantar (c.model/novo-cliente "30971065083" "Alantar the Blue" "alantar.blue@shire.com"))
(def radagast (c.model/novo-cliente "24971065583" "Radagast the Brown" "radagast.brown@shire.com"))
(c.db/adiciona-clientes! conn [gandalf alantar radagast])

(pprint
  (c.db/retorna-clientes (d/db conn)))

;adiciona clientes no banco
(def cartao-do-gandalf (c.model/novo-cartao [:cliente/id (:cliente/id gandalf)] "5150920705496483" "999" "12/21" 1000M)) ;lookup reference :)
(def cartao-do-alantar (c.model/novo-cartao [:cliente/id (:cliente/id alantar)] "4050920705496483" "995" "07/21" 650M))
(def cartao-do-radagast (c.model/novo-cartao [:cliente/id (:cliente/id radagast)] "6050920705496483" "998" "06/21" 700M))
(c.db/adiciona-cartoes! conn [cartao-do-gandalf cartao-do-alantar cartao-do-radagast])

;adiciona compras no banco
(def compra1 (c.model/nova-compra [:cartao/id (:cartao/id cartao-do-gandalf)] (clojure.instant/read-instant-date "2021-05-09T09:59:40.829") 50.01M "Padoca da Rosinha" :alimentacao))
(def compra2 (c.model/nova-compra [:cartao/id (:cartao/id cartao-do-gandalf)] (clojure.instant/read-instant-date "2021-05-21T09:59:40.829") 28.97M "Teatro dos magos" :lazer))
(def compra3 (c.model/nova-compra [:cartao/id (:cartao/id cartao-do-alantar)] (clojure.instant/read-instant-date "2021-05-21T10:59:40.829") 10.97M "Teatro dos magos" :lazer))
(c.db/adiciona-compras! conn [compra1 compra2 compra3])
;(c.db/adiciona-compras! conn [compra3])
;retorna compras com todos os dados.
(println "Todas as compras")
(c.db/retorna-compras-completa (d/db conn))

;retorna quantidades de compra por pessoa.
(c.db/retorna-compras-por-cliente (d/db conn))
(println "Cliente com mais compras" (apply max-key (c.db/retorna-compras-por-cliente (d/db conn))))

(c.db/retorna-compras-mais-cara (d/db conn))
(println "Compra mais cara" (c.db/retorna-compras-mais-cara (d/db conn)))

(c.db/clientes-sem-compra (d/db conn))
(println "Clientes sem compra" (c.db/clientes-sem-compra (d/db conn)))

(c.db/retorna-gasto-por-categoria (d/db conn))
(println "Gasto por categoria" (c.db/retorna-gasto-por-categoria (d/db conn)))

;(def compraz (ffirst (c.db/retorna-compras-completa (d/db conn))))
;
;(ffirst (c.db/retorna-compras-completa (d/db conn)))
;(:compra/valor compraz) ; obtendo o valor da compra
;(:cartao/numero (:compra/cartao compraz))                   ;obtendo o número do cartão

(println (chave-valor compra))
;implementar metodos abaixo com base no db tb
;(defn todas-as-compras
;  []
;  (c.db/todas-as-compras))
;
;(defn gasto-por-categoria
;  []
;  (->
;    (c.db/todas-as-compras)
;    (c.logic/compras-por-categoria)
;    (c.logic/gasto-total-por-categoria)))
;
;
;(defn compras-por-estabelecimento
;  [nome-estabelecimento]
;  (->
;    (c.db/todas-as-compras)
;    (c.logic/compras-por-estabelecimento nome-estabelecimento)))
;
;(defn faturas
;  []
;  (-> (c.db/todas-as-compras)
;      (c.logic/compras-mensais (.getMonth (LocalDateTime/now)))
;      (c.logic/total-mensal)))
;
;(defn fatura-por-cliente
;  [cpf-cliente]
;  (-> (c.db/todas-as-compras)
;      (c.logic/compras-por-cliente cpf-cliente)
;      (c.logic/compras-mensais (.getMonth (LocalDateTime/now)))
;      (c.logic/total-mensal)))
;
;
;(println "Todas as compras" (todas-as-compras))
;(println "Gasto por categoria: " (gasto-por-categoria))
;(println "Compras no teatro de magos " (compras-por-estabelecimento "Teatro de magos"))
;(println "Total do mês atual" (faturas))
;(println "Fatura do Alantar no mês atual" (fatura-por-cliente 30971065083))