const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

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

test("Unauthenticated user: cannot read or write user profiles or activities", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Pratama",
      username: "alice_p",
      phoneNumber: "08123456789",
      gender: "Perempuan",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("activities").get()
  );
});

test("Authenticated owner: valid Kotlin create & partial update payloads succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Mirror UserRepository.saveProfile create payload (no @DocumentId `id` field, optional nulls omitted)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      birthDate: "12 Mei 2000",
      address: "Jakarta Selatan",
      bio: "Product Designer",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Read back own profile
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Mirror partial update payload (mutable fields + updatedAt only; createdAt & userId preserved)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Alice P. Wijaya",
      bio: "Lead Glass UI Designer",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Shadow update, immutable fields, invalid bounds & future timestamps are rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Reject payload containing `@DocumentId` `id` field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      id: ALICE_UID,
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Reject ghost field `isAdmin`
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      isAdmin: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      createdAt: new Date(Date.now() + 60000),
      updatedAt: created,
    })
  );

  // Reject invalid gender enum
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Unknown",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Create valid doc first, then test immutable createdAt / userId update rejection
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      userId: BOB_UID,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Cross-user isolation: Bob cannot read, list, or modify Alice's profile or activities", async () => {
  const created = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@gmail.com",
      fullName: "Alice Pratama",
      username: "alice_gsys",
      phoneNumber: "081234567890",
      gender: "Perempuan",
      createdAt: created,
      updatedAt: created,
    });
    await db
      .collection("users")
      .doc(ALICE_UID)
      .collection("activities")
      .doc("act_1")
      .set({
        action: "Registrasi Akun",
        detail: "Pendaftaran data diri GSYS selesai",
        category: "auth",
        createdAt: created,
      });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ fullName: "Hacked" })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).delete());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("activities").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activities")
      .doc("act_2")
      .set({
        action: "Spoof",
        detail: "Spoofed entry",
        category: "security",
        createdAt: created,
      })
  );
});

test("Activities subcollection: owner can create & query, cannot update immutable logs", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activities")
      .doc("act_1")
      .set({
        action: "Registrasi Akun",
        detail: "Akun & data diri berhasil didaftarkan",
        category: "auth",
        createdAt: created,
      })
  );

  // Query aligned with Kotlin repository: users/{uid}/activities ordered by createdAt desc
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activities")
      .orderBy("createdAt", "desc")
      .get()
  );

  // Audit logs are immutable
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activities")
      .doc("act_1")
      .update({ detail: "Tampered" })
  );
});
