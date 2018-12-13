# Extra Use Cases
1. **The user can save her game state.** A button will be added to the engine front-end that enables immediate serialization of all the game data, which is saved in a separate file.
2. **The user can continue from a previously saved game.** When the game loads, the user is presented with an option to start a new game or to continue from a previous game. If no previous game was played, then a new game is started.
3. **The user can restart a game after completion within the same window.** When the game ends, the user has the option to restart the game from scratch by clicking a specific option in the Alert that appears.
4. **The user can see all her past scores on games.** Through the launcher, the user is able to access the history of all her past scores. This requires the launcher to have a "sign-in" screen, to personalize the experience per player.
5. **The user can navigate the game via a separate controller.**
6. **The user can make multiple phases graph of the game.**
From Phase Tab,the user can create new phase by clicking button. All the phases will be listed below the button, the user can work on each phases by clicking a phase from the list.
7. **The user can define different behaviors for each phase.**
By doublic click the phase node, the window pops up. The window has the groovy blocks and by linking the blocks, the user can generate lines of code.
8. **The user can define the listener between phases.**
The user can connect two phase nodes by dragging from start point to end point. It will draw the line between these. The dialog pops up and ask the user to define listener before drawing the line.
9. **The user can change grid size any time on authoring interface.**
From Menu Tool bar, the user can reassign the column and row of the grid.
10. **The user can see the executable code.**
When the user all linked the groovy blocks and if it is executable, the code will be shown on the right side of the window.
11. **The user can easily remove and create the phases or groovy blocks.**
When the user want to delete the nodes or lines, the user can delete with delete or backspace button.
12. **The user can set properties of entity.**
The user can give properties to entity such as Health Point. For this, the user can type properties' name and values of this on the window of entity creation.
13. **The user can seperate tabpane from tabs so that work on the window modal.**
If the user drag the tab from tabs, the pane will become the window modal (i.e how Chrome tabs working).
14. **The user can work on multiple authoring interfaces.**
If the user click new button from menu bars, it will open the new authoring interface window.
15. **The user cannot duplicate names on phase graph and phase nodes.**
If the user duplicate the name, it will not generate new phase or new phase node. And it gives alert dialog that the name is already existed.
16. **The user could specify the number of players in the game.**
In the process of creating a game, a pop-up window would appear to let the user choose the numebr of players such that the turn class knows how to process
17. **The user could specify the player's sequence.**
The user could order the players to adjust the sequence of the player
18. **The user could specify the winning condition of the game.**
In the groovy blocks, the user could use if else statement to check the winning condition in the end of the last node
19. **The user could see some states of the player when the mouse is hovering over the player.**
For players who have state such as blood level, there will be one window showing the state of these players
20. **The user could create multiple tiles as the base of each game.**
A pop-up window will show up to allow the user to choose the width and height for each game. For example, tic tac toe will have a 3 * 3 grid
21. **The user could drag and drop the tile to the EditView, and the tile will show up there.**
The user is able to drag a TileClass represented by a TreeItem in the TreeView to the EditView canvas, where a TileInstance corresponding to that TileClass will be created there.
22. **The user can set an avatar for each player.**
The user is able to set an avater image for each player and the image will display on the screen.

23. **The user can open and edit the XML file directly.**
Not only can the user make the game from the main window, the user can also only the XML file and edit it directly to make the game.

24. **The user can set the name for the player.**
The user can customize/pre-set name for the players involved in the game.



25. **The user can delete an existing entity.**
When the user creates an extra entity, the user can right-click on that entity to delete. This deletes the entity from the back-end database.


26. **The user can define a maximum playing time.**
The user can specify a time by which if it is extended and the winning condition is not fulfiled, the game will just end.

27. **The user can set the starting properties for each player.**
The author can pre-set properties for each player, such as the how many lives each player has.

28. **The user can add multiple images to objects in the game.**
The author can right-click on a game object and choose to edit the game object. A pop-up windown will appear and the user can add an image from his or her storage system.

29. **The user can choose what image to display on any game object given the game state.**
The user can use the grovvy tab to input the phase node that is required to select an image from a list of images that the game object class holds.

30. **The user can view what GameObjectInstances are tied to a Tile.**
The author can click on a TileIntance that is placed on the grid and see what GameObjectInstances are "tied" to that TileInstance, and edit them respectively in a new window.

31. **The user can click on a GameObject and batch apply**
The author can click on a GameObjectClass and apply on the grid by press the mouse and drag them across the grid canvas.

32. **The user can apply custom phase diagram for some specific GameObjectInstance.**
The author can apply a separate phase diagram to an object instance on the Grid such that it has a separate phase diagram from the class it is associated with.

33. **The user can delete Nodes and the GameObjectInstances easily.**
The user should be able to remove a JavaFx node from the editing Grid and the GameObjectInstance from the CRUD manager at the same time.

34. **The user can edit the properties of the tile from the edit grid view.**
The user can right-click on the instance of the tile and modify the properties name or change their default values.

35. **The user can view a list of the game object instances they have added to the grid pane.**
The user can choose from a drop-down menu to view a list of objects they have added to the grid edit pane.

36. **The user can assign entities to the player**
The user can choose the owner of entity from entity editor.

37. **The user can set the background music sound**
On the BGM setting under menu bar, the user can choose sound file and play the uploaded sound.

38. **The user can resize the individual cell of the game**
Under menu bar settings, the user can resize the individual cells' width and height.

39. **The user can make win condition**
Win condition Tab allows the user to create the win condition which will check during all turns of the game.

40. **The user can generate multiple tiles.**
The user can add tile, size, mode, probability under tile setting of menu bar settings.
