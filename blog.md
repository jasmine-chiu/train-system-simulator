

3.1.1, 3.1.2, 3.1.3
* Implemented factory pattern in TrainsController.java for cleaner type assignment and calling of stations
* Originally had "checkMaxTrain" as a helper function, changed this to call from within the Station class
* Modularised a "findRouteType" helper function to determine routeType
* Created an enum for RouteType to better categorise
* Created an enum for Direction for better control over trains
* Implemented factory pattern in TrainsController.java for cleaner type assignment and calling of trains
* Sorting function for lexicographical ordering of trains added as helper function in "IdHelper.java"

3.1.4, 3.1.5, 3.1.6
- Admittedly, code is quite repetitive => could create filter function for shortening

3.1.7
* Created a helper function for "createLoadInfo" to use over multiple functions, further modularising functionality and meaning it isn't a list that needs to be stored/updated => more maintainable and instantiated only when called

3.2.1 SIMULATE
* Combined repetitive search for ID corresponding with trains/stations/tracks to "IdHelper.java" helper functions, removing lots of repetitive code from TrainsController
* Ensured changes were made only with getters and setters, almost did not account for that when changing Load lists
* Created a new set of helper functions, "SimulateHelper.java" to actively continue breaking down functions into more finetuned and deconstructable actions
    * These helper functions create all the checks needed to determine, for each train, what state they are in e.g. onTrack, inbound, stopped
    * They also help break down the different steps encompassed by each state, e.g. onboarding, offloading, perishing for stopped trains
* Functions call from within the class, using getters and setters to safely change values
    * e.g. moving findNextStation to within the train class for a cleaner use of its variables, without needing to add any values to the parameters of the function call
* Utilising a lot of modularised boolean checks in order to further break down code, and to add reusability to these functions without repetition
* using instanceof instead of ClassContains
- structure feels a bit off in terms of logic, would have liked to refine if given more time

3.2.2
* Created an Enum for LoadType to better categorise, adds greater clarity than assigning by String
* For boardAllowance, moved this into Train class and combined the checking functions for checking valid weight and if train could take this loadType
* Moved some helper functions into respective classes, e.g. loadCheck into station, to lessen passing variables between functions => safer coding, easier to pinpoint issues if they were to come up
* Replaced helper which gave boolean for whether dest station could unload given LoadType with utilising same function as start station check
* Ensured adding and removing loads were done with the getters and setters for trains/stations
* Used a sorting helper function for loadIds to help with lexicographical order
* Storing canLoad for stations and trains within constructors to better keep classes self-contained

3.2.3
* Broke down route finding into distinct and reusable sections of route calculations, e.g.
    * reusing a baseline linear route to encompass the largest portion of track each cargoRoute would contain
    * breaking down cases, finding adjacent  between cyclical and linear
    * backwards equivalent of a linear route is just the reverse order of a forward linear route
* Having perishing occur from within the station/train the load exists within, using getters each time to get minsTillPerish
* adjustSpeed moved to a different function within train, gives it more versatility

FROM THIS ASSIGNMENT,
* I feel much more confident with Java
* I understand the value of modularity in code; this is the first assignment I've paid proper attention into trying to make my code more succinct by combining functions, using helpers and streamlining logic
* Though my understanding of design patterns and architecture are not fully evolved, I can see the utility they provide
* I'm interested to see how interfaces would come into play as well

IF I HAD MORE TIME,
* Refactoring PerishableCargo to have an is-a Cargo subclass relationship, not being a subclass of load - difficult because of the LoadType enum I created
* Refactoring cargoWeight to only be included for Cargo and Bullet trains
* Refactoring "TrainType" into an enum used
* Creating more classes for passed in parameters for different functions, e.g. startId, destId, loadId - class inputs are long and repetitive
* Refactor the cargoRoute calculation while it depends on direction and routeType, more neatly modularising the route calculation code and its helper functions
* Having a more logical order and purpose for "IdHelper" - started out as an IdHelper, ended up being for all non-simulate helper functions