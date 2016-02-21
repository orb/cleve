# cleve

A Clojure project accessing the EVE Online CREST API.

At this point the app only authenticates via OAUTH2, and it doesn't
even do that correctly. But, we have to start somewhere...

## config

Update `resources/auth.edn` with your EVE OAUTH secrets, or (better)
copy the file to `dev-resources/auth.edn` and make your updated to
avoid editting a git-tracked file.

## running

`lein ring server-headless`
