# Plan for VIS-142

Visits have no status today. Staff cannot mark a visit complete or cancelled.

This plan comes from a read of `docs/tickets/VIS-142.md` and a search of the code. No code change is in this file.

## What the search found

The app reads and writes visits in these places:

- `src/main/java/org/springframework/samples/petclinic/owner/Visit.java` stores the date and the description.
- `Pet.java` holds the visit list. `Owner.java` adds a visit to a pet.
- `OwnerRepository.java` saves the owner. The save also saves visits. There is no visit repository.
- `VisitController.java` creates a new visit. `OwnerController.java` shows the owner page.
- `src/main/resources/templates/owners/ownerDetails.html` shows the date and the description.
- `src/main/resources/templates/pets/createOrUpdateVisitForm.html` is the new-visit form.
- Tests: `VisitControllerTests.java`, `OwnerControllerTests.java`, `ClinicServiceTests.java`.
- Message files under `src/main/resources/messages`:
  - `messages.properties`
  - `messages_de.properties`
  - `messages_en.properties` (this file is empty on purpose)
  - `messages_es.properties`
  - `messages_fa.properties`
  - `messages_hi.properties`
  - `messages_ja.properties`
  - `messages_ko.properties`
  - `messages_pt.properties`
  - `messages_ru.properties`
  - `messages_tr.properties`
- Database files:
  - `src/main/resources/db/h2/schema.sql` and `src/main/resources/db/h2/data.sql`
  - `src/main/resources/db/mysql/schema.sql` and `src/main/resources/db/mysql/data.sql`
  - `src/main/resources/db/postgres/schema.sql` and `src/main/resources/db/postgres/data.sql`

H2 and MySQL `data.sql` insert visits by position. The four values are id, pet id, date, and description. Postgres `data.sql` names the columns.

A test named `I18nPropertiesSyncTest` checks that every key in `messages.properties` is also in each language file. It skips `messages_en.properties`.

## Questions

1. The old seed visits are in the past. Should those rows be `COMPLETED`, or `SCHEDULED`?
2. The ticket says to show the status. It does not ask for a button to complete or cancel a visit. Is a label enough?
3. Should the page show the word `SCHEDULED`, or a translated label for each state?

## Choices for this build

- Old seed rows are `COMPLETED`. New visits start as `SCHEDULED`.
- Show the status on the owner page. Do not add a button. The rule stays on the visit object.
- The status cell shows the enum name. The column title uses a message key named `visitStatus`.
- Leave `messages_en.properties` empty. The file says it is empty on purpose. Add `visitStatus` to the other ten message files.

## Steps

1. Create branch `feature/vis-142-visit-status` from `demo/setup`.
2. Add column `status` to the H2, MySQL, and Postgres schema files. The column is text, required, and defaults to `SCHEDULED`.
3. Add the status value to every visits row in H2 `data.sql`, MySQL `data.sql`, and Postgres `data.sql`. Use `COMPLETED` for the old rows. H2 and MySQL stay positional, so the new value is the last value in each row.
4. Run `./mvnw spring-javaformat:apply`, then `./mvnw test`.
5. Add enum `VisitStatus` with `SCHEDULED`, `COMPLETED`, and `CANCELLED`. Only `SCHEDULED` may move to `COMPLETED` or `CANCELLED`.
6. Add `status` on `Visit`. A new visit starts as `SCHEDULED`. Put `changeStatus`, `complete`, and `cancel` on `Visit`, not on a controller.
7. Add `VisitTests` for a new visit, both allowed changes, and every blocked change.
8. Run format, then tests.
9. Add a Status column on `ownerDetails.html`.
10. Add key `visitStatus` to all message files except the empty English file.
11. Run format, then tests. All old tests must still pass.
