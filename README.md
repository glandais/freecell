# freecell

## Scores

| From       | To         | Condition / comment                                                                       | Score |
|------------|------------|-------------------------------------------------------------------------------------------|-------|
| Tableau    | Tableau    | Not the complete suite, avoid                                                             | 300   |
| Foundation | Tableau    | Not very interesting                                                                      | 150   |
| Pile       | Pile       | Not very interesting                                                                      | 100   |
| Tableau    | Tableau    | n hidden cards in from, interesting (show new card, bonus for the most hidden card count) | -n    |
| Tableau    | Foundation | Nice move                                                                                 | -50   |
| Tableau    | Tableau    | No hidden card in from, not king at top, interesting (free space)                         | -80   |
| Pile       | Tableau    | Move a king to an empty tableau                                                           | -90   |
| Tableau    | Tableau    | Move a suite starting with a king to an empty tableau                                     | -100  |
