import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated access is denied across user profile and subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("journals").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("journals")
      .doc("j_1")
      .set({
        userId: ALICE_UID,
        date: "2026-11-18",
        content: "Unauthorized entry",
        createdAt: now,
        updatedAt: now,
      })
  );
});

test("Owner can create and partially update profile and journals while rejecting ghost/future fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice",
      onboardingCompleted: true,
      themeId: "MIDNIGHT_INDIGO",
      fontScale: 1.0,
      reduceMotion: false,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .update({
        themeId: "SAGE_FOREST",
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Reject ghost field
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .update({
        isAdmin: true,
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Journal create + partial update
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("journals")
      .doc("entry_1")
      .set({
        userId: ALICE_UID,
        date: "2026-11-18",
        content: "Feeling calmer after breathing.",
        tags: ["Calm", "Sleep"],
        moodRating: 4,
        createdAt: created,
        updatedAt: created,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("journals")
      .doc("entry_1")
      .update({
        sentiment: "Hopeful",
        updatedAt: new Date(Date.now() - 500),
      })
  );

  // Reject immutable createdAt mutation and future timestamp
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("journals")
      .doc("entry_1")
      .update({
        createdAt: new Date(Date.now() - 200),
        updatedAt: new Date(Date.now() - 200),
      })
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("moods")
      .doc("2026-11-18")
      .set({
        userId: ALICE_UID,
        date: "2026-11-18",
        mood: 4,
        createdAt: new Date(Date.now() + 60000),
        updatedAt: new Date(Date.now() + 60000),
      })
  );
});

test("Cross-user reads and writes are strictly rejected", async () => {
  const created = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("energyLogs")
      .doc("2026-11-18")
      .set({
        userId: BOB_UID,
        date: "2026-11-18",
        mentalBattery: 70,
        physicalBattery: 65,
        createdAt: created,
        updatedAt: created,
      });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("energyLogs")
      .doc("2026-11-18")
      .get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("energyLogs")
      .doc("2026-11-18")
      .update({ mentalBattery: 10, updatedAt: new Date(Date.now() - 1000) })
  );
});

test("Subcollection list queries succeed for owner and validate bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("habits").get()
  );

  // Out-of-bounds battery fails
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("energyLogs")
      .doc("2026-11-18")
      .set({
        userId: ALICE_UID,
        date: "2026-11-18",
        mentalBattery: 150,
        physicalBattery: 50,
        createdAt: now,
        updatedAt: now,
      })
  );

  // Valid affirmation, habit, sleep, trigger, quiz
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("affirmations")
      .doc("aff_1")
      .set({
        userId: ALICE_UID,
        text: "I meet today with patience.",
        favorite: true,
        pinned: true,
        custom: true,
        createdAt: now,
        updatedAt: now,
      })
  );
});
