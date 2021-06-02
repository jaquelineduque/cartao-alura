(ns cartao.logic-test
  (:require [clojure.test :refer :all]
            [cartao.logic :as c.logic]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.properties :as prop]))

(deftest valor-compra
  (testing "valor preenchido"
    (is (= (c.logic/valor-por-compra {:valor 60.72})
           60.72)))

  (testing "sem valor preenchido"
    (is (= (c.logic/valor-por-compra {})
           0)))

  (testing "valor negativo"
    (is (= (c.logic/valor-por-compra {:valor -10})
           -10)))

  (testing "string"
    (is (= (c.logic/valor-por-compra {:valor "aaa"})
           "aaa")))
  )

(def string-aleatoria
  (gen/fmap clojure.string/join
            (gen/vector gen/char-alphanumeric 5 10)))

(defn compra-gen []
  {(keyword (gen/generate string-aleatoria)) (gen/generate string-aleatoria)})


(defn compra-com-mais-dados [valor]
  {:valor
   valor
   (keyword (gen/generate string-aleatoria))
   (gen/generate string-aleatoria)})

(deftest generativo
  (testing "generativo sem valor"
    (is (= (c.logic/valor-por-compra (compra-gen))
           0)))

  (testing "valor gerado"
    (let [valor (gen/generate gen/double)]
      (is (= (c.logic/valor-por-compra {:valor valor})
             valor))))

  (testing "compra com mais dados"
    (let [valor (gen/generate gen/double)]
      (is (= (c.logic/valor-por-compra (compra-com-mais-dados valor))
             valor))))
  )

;(defspec teste-carga 10
;         (prop/for-all []
;                       (let [valor (gen/generate gen/double)]
;                         (println "valor" valor)
;                         (is (= (c.logic/valor-por-compra (compra-com-mais-dados valor))
;                                valor)))
;                       )
;         )

(defspec teste-carga 100
         (prop/for-all [valor gen/double]
                       (is (= (c.logic/valor-por-compra (compra-com-mais-dados valor))
                              valor))))

(def compras-teste [{:compra {:valor 10.75M :categoria :alimentacao}}
                    {:compra {:valor 22.30M :categoria :alimentacao}}
                    {:compra {:valor 7.87M :categoria :alimentacao}}
                    {:compra {:valor 20M :categoria :lazer}}])

(deftest compras-categoria
  (testing "compra por categoria"
    (is (and (contains? (c.logic/compras-por-categoria compras-teste)
                        :alimentacao)
             (contains? (c.logic/compras-por-categoria compras-teste)
                        :lazer)))))


(def valor-alimentacao [:alimentacao [{:valor 10.75M, :categoria :alimentacao}
                                       {:valor 22.3M, :categoria :alimentacao}
                                       {:valor 7.87M, :categoria :alimentacao}]])

(def valor-lazer [:lazer [{:valor 20M, :categoria :lazer}]])

(deftest valor-categoria
  (testing "valor alimentacao"
    (is (= (:valor (c.logic/valor-por-categoria valor-alimentacao))
           40.92M)))
  (testing "valor lazer"
    (is (= (:valor (c.logic/valor-por-categoria valor-lazer))
           20M)))
  (testing "valor sem valores"
    (is (= (:valor (c.logic/valor-por-categoria []))
           0))))

(def compras-estabelecimento [{:compra {:valor 10.75M :estabelecimento "Padoca da Rosinha"}}
                    {:compra {:valor 22.30M :estabelecimento "Teatro dos magos"}}
                    {:compra {:valor 7.87M :estabelecimento "Padoca da Rosinha"}}
                    {:compra {:valor 20M :estabelecimento "Padoca da Rosinha"}}])

(deftest estabelecimento
          (testing "Quantidade padaria"
            (is (= (count (c.logic/compras-por-estabelecimento compras-estabelecimento "Padoca da Rosinha"))
                   3)))
          (testing "Teatro"
            (is (= (count (c.logic/compras-por-estabelecimento compras-estabelecimento "Teatro dos magos"))
                   1)))
          (testing "Inexistente"
            (is (= (count (c.logic/compras-por-estabelecimento compras-estabelecimento "Local inexistente"))
                   0))))



