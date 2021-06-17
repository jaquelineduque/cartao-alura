(ns cartao.model)

(defn uuid [] (java.util.UUID/randomUUID))

(defn novo-cliente
  [cpf nome email]
  {:cliente/cpf   cpf
   :cliente/id    (uuid)
   :cliente/nome  nome
   :cliente/email email
   })

(defn novo-cartao
  [id-cliente numero-cartao cvv validade limite]
  {:cartao/id         (uuid)
   :cartao/id-cliente id-cliente
   :cartao/numero     numero-cartao
   :cartao/cvv        cvv
   :cartao/validade   validade
   :cartao/limite     limite})


(defn nova-compra
  [id-cartao data valor estabelecimento categoria]
  {:compra/id              (uuid)
   :compra/id-cartao       id-cartao
   :compra/data            data
   :compra/valor           valor
   :compra/estabelecimento estabelecimento
   :compra/categoria       categoria})