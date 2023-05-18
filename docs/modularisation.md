### Modularisation

#### Summary

In the app two types of modules can be distinguished - library modules and feature modules.
Library modules provides tools and resources for feature modules (lokalise, tracker, logger, feature flags).
Features modules represents one screen or one feature which can contain multiple screens (challenges, delivery steps, settings).
Information about common libraries like Retrofit, Dagger is stored in parent build.gradle file to have the same library versions in all modules.

#### New module creation

##### Feature module
* Create new "Android library" module
* Add "core" module and "lokalise" as dependencies (first one provides objects from app module, second one provides translations)
* Create dagger component (every feature module should have its own dagger component to inject dependencies)
* Add new feature module as dependency to app module

##### Library module
* Create new "Android library" module or pure kotlin module (it is good practice to prefer pure kotlin module over android library if we don't need android resources)
* Add new library module to feature module as dependency
