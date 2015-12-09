(ns metabase.test.data.druid
  (:require metabase.driver.druid
            [metabase.test.data.interface :as i])
  (:import metabase.driver.druid.DruidDriver))

(extend DruidDriver
  i/IDatasetLoader
  i/IDatasetLoaderDefaultsMixin)
