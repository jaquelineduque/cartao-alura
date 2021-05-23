(ns cartao.db
  (:require [datomic.api :as d])
  (:use [clojure pprint]))

(def db-uri "datomic:dev://localhost:4334/cartao")

(def schema-compras [
             ;Clientes
             {:db/ident       :cliente/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "id do cliente"}
             {:db/ident       :cliente/cpf
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "CPF do cliente"}
             {:db/ident       :cliente/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Nome do cliente"}
             {:db/ident       :cliente/email
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Email do cliente"}

             ;cartão
             {:db/ident       :cartao/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "id do cartao"}
             {:db/ident       :cartao/numero
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Número do cartão"}
             {:db/ident       :cartao/cvv
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "CVV"}
             {:db/ident       :cartao/validade
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Validade (MM/AA)"}
             {:db/ident       :cartao/limite
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Limite"}
             {:db/ident       :cartao/id-cliente
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc         "id do cliente"}

             ;compras
             {:db/ident       :compra/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/doc         "id da compra"}
             {:db/ident       :compra/data
              :db/valueType   :db.type/instant
              :db/cardinality :db.cardinality/one
              :db/doc         "Data da compra"}
             {:db/ident       :compra/valor
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Valor da compra"}
             {:db/ident       :compra/estabelecimento
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Estabelecimento da compra"}
             {:db/ident       :compra/categoria
              :db/valueType   :db.type/keyword
              :db/cardinality :db.cardinality/one
              :db/doc         "Categoria da compra"}
             {:db/ident       :compra/id-cartao
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/doc         "id do cartão"}])

(defn cria-schema!
  [conn schema-compras]
  (d/transact conn schema-compras))

(defn abre-conexao!
  []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco!
  []
  (d/delete-database db-uri))

(defn adiciona-clientes!
  [conn clientes]
  (d/transact conn clientes))

(defn adiciona-cartoes!
  [conn cartoes]
  (d/transact conn cartoes))

(defn adiciona-compras!
  [conn compras]
  (d/transact conn compras))

(defn retorna-clientes
  [db]
  (d/q '[:find (pull ?cliente [:cliente/id :cliente/cpf :cliente/nome :cliente/email])
         :where [?cliente :cliente/id]] db))

(defn retorna-cartoes
  [db]
  (d/q '[:find (pull ?cartao [:cartao/id :cartao/numero :cartao/cvv
                              :cartao/validade :cartao/limite :cartao/id-cliente])
         :where [?cartao :cartao/id]] db))

(defn retorna-compras
  [db]
  (d/q '[:find (pull ?compra [:compra/id :compra/data :compra/valor
                              :compra/estabelecimento :compra/categoria :compra/id-cartao])
         :where [?compra :compra/id]] db))

(defn retorna-compras-completa
  [db]
  (d/q '[:find (pull ?compra [ :compra/id :compra/data :compra/valor
                              :compra/estabelecimento :compra/categoria {[:compra/id-cartao :as :compra/cartao] [:cartao/id  :cartao/numero :cartao/cvv
                                                                                            :cartao/limite :cartao/validade
                                                                                            {[:cartao/id-cliente :as :cartao/cliente] [
                                                                                                                 :cliente/id
                                                                                                                 :cliente/cpf
                                                                                                                 :cliente/nome
                                                                                                                 :cliente/email]}]
                                                                          }])
         :where [?compra :compra/id]] db))

(defn retorna-compras-por-cliente
  [db]
  (d/q '[:find ?id-cartao ?id-cliente (count ?id-compra)
         :with ?compra
         :keys cartao cliente quantidade-compra
         :where [?compra :compra/id ?id-compra]
                [?compra :compra/id-cartao ?id-cartao]
                [?id-cartao :cartao/id-cliente ?id-cliente]]
       db))


;forma abaixo parece não ser a mais eficiente [diversas cláusulas no where]. Alguma alternativa?
(defn retorna-compras-por-cliente
  [db]
  ;(d/q '[:find (pull ?id-cliente [:cliente/nome]) (pull ?id-compra [(count) :as :cliente/quantidade])
   (d/q '[:find ?nome (count ?id-compra)
         :with ?compra
         :keys nome quantidade-compra
         :where [?compra :compra/id ?id-compra]
            [?compra :compra/id-cartao ?id-cartao]
            [?id-cartao :cartao/id-cliente ?id-cliente]
            [?id-cliente :cliente/nome ?nome]]
       db))

(defn retorna-gasto-por-categoria
  [db]
  ;(d/q '[:find (pull ?id-cliente [:cliente/nome]) (pull ?id-compra [(count) :as :cliente/quantidade])
  (d/q '[:find ?categoria (sum ?valor)
         :with ?compra
         :keys nome quantidade-compra
         :where [?compra :compra/id ?id-compra]
         [?compra :compra/valor ?valor]
         [?compra :compra/categoria ?categoria]]
       db))

;(defn retorna-compras-mais-cara
;  [db]
;  (d/q '[:find (pull ?compra [*])
;         :where [(q '[:find (max ?valor)
;                      :where [_ :compra/valor ?valor]]
;                    $) [[?valor]]]
;         [?compra :compra/valor ?valor]]
;       db))

(defn retorna-compras-mais-cara
  [db]
  (d/q '[:find ?nome ?valor
         :keys nome valor
         :where [(q '[:find (max ?valor)
                      :where [_ :compra/valor ?valor]]
                    $) [[?valor]]]
         [?compra :compra/valor ?valor]
         [?compra :compra/id-cartao ?id-cartao]
         [?id-cartao :cartao/id-cliente ?id-cliente]
         [?id-cliente :cliente/nome ?nome]]
       db))

(defn clientes-sem-compra
  [db]
  (d/q '[:find ?nome
         :keys nome
         :where
         [?cartao :cartao/id-cliente ?cliente]
         [?cliente :cliente/nome ?nome]
         (not [_ :compra/id-cartao ?cartao])
         ]
       db))


  (def compra1 {:cliente {:cpf   14971065083
                          :nome  "Gandalf the Gray"
                          :email "gandalf.gray@shire.com"}
                :cartao  {:numero   5150920705496483
                          :cvv      999
                          :validade "12/21"
                          :limite   1000}
                :compra  {:data            "2021-05-09T09:59:40.829"
                          :valor           50.01
                          :estabelecimento "Padoca da Rosinha"
                          :categoria       :alimentacao}})

  (def compra2 {:cliente {:cpf   14971065083
                          :nome  "Gandalf the Gray"
                          :email "gandalf.gray@shire.com"}
                :cartao  {:numero   5150920705496483
                          :cvv      999
                          :validade "12/21"
                          :limite   1000}
                :compra  {:data            "2021-05-21T09:59:40.829"
                          :valor           28.97
                          :estabelecimento "Teatro de magos"
                          :categoria       :lazer}})

  (def compra3 {:cliente {:cpf   30971065083
                          :nome  "Alantar the Blue"
                          :email "alantar.blue@shire.com"}
                :cartao  {:numero   4050920705496483
                          :cvv      995
                          :validade "07/21"
                          :limite   650}
                :compra  {:data            "2021-05-09T09:59:40.829"
                          :valor           57.00
                          :estabelecimento "Livraria Arda"
                          :categoria       :estudo}})

  (def compra4 {:cliente {:cpf   30971065083
                          :nome  "Alantar the Blue"
                          :email "alantar.blue@shire.com"}
                :cartao  {:numero   4050920705496483
                          :cvv      995
                          :validade "07/21"
                          :limite   650}
                :compra  {:data            "2021-07-09T09:59:40.829"
                          :valor           87.00
                          :estabelecimento "Livraria Arda"
                          :categoria       :estudo}})


  (def compra5 {:cliente {:cpf   14971065083
                          :nome  "Gandalf the Gray"
                          :email "gandalf.gray@shire.com"}
                :cartao  {:numero   5150920705496483
                          :cvv      999
                          :validade "12/21"
                          :limite   1000}
                :compra  {:data            "2021-06-03T09:59:40.829"
                          :valor           60.72
                          :estabelecimento "Mercadinho do Sauron"
                          :categoria       :alimentacao}})

  (def compra6 {:cliente {:cpf   24971065583
                          :nome  "Radagast the Brown"
                          :email "radagast.brown@shire.com"}
                :cartao  {:numero   6050920705496483
                          :cvv      998
                          :validade "06/21"
                          :limite   700}
                :compra  {:data            "2021-06-08T09:59:40.829"
                          :valor           28.00
                          :estabelecimento "Teatro de magos"
                          :categoria       :lazer}})



  (defn todas-as-compras
    []
    [compra1 compra2 compra3 compra4 compra5])