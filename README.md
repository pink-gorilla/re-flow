# re-flow [![GitHub Actions status |pink-gorilla/clj-service](https://github.com/pink-gorilla/re-flow/workflows/CI/badge.svg)](https://github.com/pink-gorilla/re-flow/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/re-flow.svg)](https://clojars.org/org.pinkgorilla/re-flow)

- re-flow allows to consume missionary flows in clojurescript.
- flows can be consumed:
  - either from a sci-clojurescipt code via re-flow.core/re-flow
  - or from a reagent/react view via re-flow.core/flow-ui
- the idea is that clj-services are "exposed" for consumption for the 
  browser via clj-service library. Any clojure function that is exposed
  this way (this be permissioned managed via permission library) must
  return a flow, and this flow can be consumed.
- the idea is to extend request-reply style communication between clojure
  and the browser (clojurescipt) with publish/subscribe model and use
  missionarys design to clean up resources when they are no longer needed.
- the benefits to this approach are:
  - keep using reagent/react
  - all pure render-functions from react/reagent can be used to render
    updates that come in via a re(directed)-flow.
  - render functions can be defined via interpreted sci-clojurescript 
    code, and therefore can be extended at runtime by arbitrary code that
    should defined the view.

stage: exploratory/experimental 

 # syntax

```
 (require '[re-flow.core :refer [re-flow flow-ui]])   

 (re-flow 'demo.service/a-seed) 
 ; this returns a missionary flow that has the same data as the
 ; flow of demo.service/a-seed would produce
 
  [flow-ui {:clj 'demo.service/quotes
            :args []
            :render 'demo.ui/quotes-ui}]]
 ; this returns a reagent render function that on each update of the
 ; flow renders html that is produced by evaluating demo.ui/quotes-ui.
 
 
```

# demo

```
  cd demo
  clj -X:webly:npm-install
  clj -X:webly:compile
  clj -X:webly:run
```






  

