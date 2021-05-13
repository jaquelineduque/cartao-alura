(ns cartao.db)

(def compra1 {:cliente {:cpf   14971065083
                        :nome  "Gandalf the Gray"
                        :email "gandalf.gray@shire.com"}
              :cartao  {:numero   5150920705496483
                        :cvv      999
                        :validade "12/21"
                        :limite   1000}
              :compra  {:data            "2021-02-09T09:59:40.829-00:00"
                        :valor           50.01
                        :estabelecimento "Padoca da Rosinha"
                        :categoria       :alimentacao}})

(def compra3 {:cliente {:cpf   14971065083
                        :nome  "Gandalf the Gray"
                        :email "gandalf.gray@shire.com"}
              :cartao  {:numero   5150920705496483
                        :cvv      999
                        :validade "12/21"
                        :limite   1000}
              :compra  {:data            "2021-03-09T17:23:41.829-00:00"
                        :valor           28.97
                        :estabelecimento "Teatro de magos"
                        :categoria       :lazer}})

(def compra5 {:cliente {:cpf   30971065083
                        :nome  "Alantar the Blue"
                        :email "alantar.blue@shire.com"}
              :cartao  {:numero   4050920705496483
                        :cvv      995
                        :validade "07/21"
                        :limite   650}
              :compra  {:data            "2021-02-10T03:59:41.829-00:00"
                        :valor           57.00
                        :estabelecimento "Livraria Arda"
                        :categoria       :estudo}})

(def compra2 {:cliente {:cpf   14971065083
                        :nome  "Gandalf the Gray"
                        :email "gandalf.gray@shire.com"}
              :cartao  {:numero   5150920705496483
                        :cvv      999
                        :validade "12/21"
                        :limite   1000}
              :compra  {:data            "2021-02-09T19:59:41.829-00:00"
                        :valor           60.72
                        :estabelecimento "Mercadinho do Sauron"
                        :categoria       :alimentacao}})

(def compra4 {:cliente {:cpf   24971065583
                        :nome  "Radagast the Brown"
                        :email "radagast.brown@shire.com"}
              :cartao  {:numero   6050920705496483
                        :cvv      998
                        :validade "06/21"
                        :limite   700}
              :compra  {:data            "2021-03-09T17:24:41.829-00:00"
                        :valor           28.00
                        :estabelecimento "Teatro de magos"
                        :categoria       :lazer}})


(defn todas-as-compras
  []
  [compra1 compra2 compra3 compra4 compra5])