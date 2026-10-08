# 📱 Detox — Guide d'Architecture, Déploiement & Manuel Complet

Ce document détaille l'intégralité du fonctionnement technique de l'application Android **Detox**, son architecture logicielle, le fonctionnement du moteur de déconnexion, ainsi que le guide étape par étape pour compiler, installer (sideload/APK) et configurer l'application sur un smartphone Android (avec un focus particulier sur les surcouches agressives comme **Xiaomi / MIUI / HyperOS**).

---

## 1. Table des matières

1. [Vue d'ensemble & Philosophie](#1-vue-densemble--philosophie)
2. [Cartographie de l'Arborescence du Projet](#2-cartographie-de-larborescence-du-projet)
3. [Détail des Modules & Rôles Techniques](#3-détail-des-modules--rôles-techniques)
4. [Moteurs Métier (Kotlin Pur)](#4-moteurs-métier-kotlin-pur)
5. [Couche Système & Fiabilité Arrière-Plan](#5-couche-système--fiabilité-arrière-plan)
6. [Procédure de Déploiement Pas-à-Pas (Build APK & Install)](#6-procédure-de-déploiement-pas-à-pas-build-apk--install)
7. [Guide Spécifique Xiaomi / HyperOS / MIUI](#7-guide-spécifique-xiaomi--hyperos--miui)
8. [Évolution & Prochaines Étapes](#8-évolution--prochaines-étapes)

---

## 1. Vue d'ensemble & Philosophie

**Detox** est une application Android native pensée pour transformer la réduction du temps d'écran en un jeu gratifiant :
- **Local d'abord** : Aucune donnée ne quitte le téléphone (pas de cloud, pas d'analytics externes).
- **Tout est paramétrable** : Les poids des applications, les pénalités par déverrouillage, les seuils de niveau et les conditions de blocage sont stockés dans un document de configuration JSON (`DetoxConfig`).
- **Garde-fous stricts** : Les applications vitales (téléphone, paramètres d'urgence, réglages système, Detox) ne sont jamais bloquées.
- **Approche bienveillante** : Blocage basé sur la friction (délai de respiration de 15s) et récompense positive (+XP résistance) plutôt qu'une punition frustrante.

---

## 2. Cartographie de l'Arborescence du Projet

```
c:\Users\Champeley\Desktop\AppT\
├── gradle/
│   └── libs.versions.toml             # Gradle Version Catalog (AGP, Compose, Room, etc.)
├── settings.gradle.kts                 # Déclaration des modules du projet
├── build.gradle.kts                    # Configuration Gradle racine
├── core/
│   ├── model/                          # Data classes pures & Serialization JSON
│   │   └── src/main/kotlin/com/detox/core/model/
│   │       ├── AppSession.kt          # Modèle de session et entrées journalières
│   │       └── DetoxConfig.kt         # Structure complète de configuration
│   ├── domain/                         # Moteurs algorithmiques 100% Kotlin (sans dépendance Android)
│   │   ├── src/main/kotlin/com/detox/core/domain/
│   │   │   ├── ScoreEngine.kt         # Calcul du score journalier & de l'XP
│   │   │   ├── LevelEngine.kt         # Niveaux exponentiels & tiers graphiques
│   │   │   ├── SessionRebuilder.kt    # Reconstitution des sessions via UsageEvents
│   │   │   └── BlockPolicyEvaluator.kt# Évaluation des règles de verrouillage
│   │   └── src/test/kotlin/           # Tests unitaires JUnit (Score, Level, Sessions)
│   ├── data/                           # Persistance Room & DataStore
│   │   └── src/main/kotlin/com/detox/core/data/
│   │       ├── local/
│   │       │   ├── entity/Entities.kt # Entités Room (app_session, unlock_event, etc.)
│   │       │   ├── dao/DetoxDao.kt    # Requêtes réactives Flow / Coroutines
│   │       │   └── db/DetoxDatabase.kt# Base de données Room v1
│   │       └── repository/
│   │           ├── UsageRepository.kt # Accès aux données d'usage
│   │           └── ConfigRepository.kt# Gestion DataStore + export/import JSON
│   ├── system/                         # Services Android d'arrière-plan & OEM
│   │   ├── src/main/kotlin/com/detox/core/system/
│   │   │   ├── collector/UsageCollector.kt           # Lecteur d'événements UsageStatsManager
│   │   │   ├── service/TrackingService.kt            # Foreground Service + BroadcastReceiver unlock
│   │   │   ├── service/BlockerAccessibilityService.kt# Interception d'ouverture d'apps
│   │   │   ├── service/BootReceiver.kt               # Relance au démarrage du téléphone
│   │   │   └── oem/
│   │   │       ├── XiaomiHelper.kt                   # Intentions MIUI/HyperOS (Autostart, Batterie)
│   │   │       └── DiagnosticManager.kt              # Statut en direct des permissions
│   │   └── src/main/res/xml/accessibility_service_config.xml # Déclaration respect vie privée
│   └── designsystem/                   # Design Tokens, Canvas et Composants Compose
│       └── src/main/kotlin/com/detox/core/designsystem/
│           ├── theme/Color.kt         # Palette Braise / Sombre néon
│           └── component/LevelProgressRing.kt # Anneau d'XP animé avec gradient balayé
├── feature/
│   └── blocker/                        # Écran de verrouillage / Pause
│       └── src/main/kotlin/com/detox/feature/blocker/
│           └── BlockActivity.kt        # Compte à rebours respiratoire & Mode Urgence
└── app/                                # Application Android & Navigation
    ├── src/main/AndroidManifest.xml    # Permissions système & services déclarés
    └── src/main/kotlin/com/detox/app/
        ├── MainActivity.kt             # Dashboard visuel Jetpack Compose
        └── DiagnosticScreen.kt         # Écran d'audit de santé & compatibilité Xiaomi
```

---

## 3. Détail des Modules & Rôles Techniques

| Module | Responsabilité | Dépendances majeures |
|---|---|---|
| `:core:model` | Contrats de données, sérialisation de la config JSON. | `kotlinx-serialization-json` |
| `:core:domain` | Algorithmes purs, formule mathématique du score, progression de niveau, règles de blocage. Zéro dépendance Android. | Kotlin standard, `kotlinx-coroutines-core` |
| `:core:designsystem` | Composants UI réutilisables, animations Canvas, palette visuelle sombre néon. | Jetpack Compose BOM, Material 3 |
| `:core:data` | Persistance locale de l'historique et des statistiques. | AndroidX Room, DataStore Preferences |
| `:core:system` | Intégration bas niveau Android (UsageStats, Accessibility, Receivers, correctifs OEM Xiaomi). | Android SDK, WorkManager |
| `:feature:blocker` | Activité de friction respiratoire affichée lors du blocage d'une app. | Jetpack Compose, `:core:designsystem` |
| `:app` | Assemblage de l'APK, navigation, écrans principaux (`MainActivity`, `DiagnosticScreen`). | Tous les sous-modules |

---

## 4. Moteurs Métier (Kotlin Pur)

### Formule de calcul du score (`ScoreEngine`)
Le score journalier est borné de 0 à 100 :
$$\text{Score} = \max\left(0, \min\left(100, 100 - (\text{unlocks} \times 0.5) - \sum (\text{minutes}_{\text{app}} \times w_{\text{app}}) + \text{bonus}_{\text{quota}}\right)\right)$$

- Poids par défaut : Réseaux sociaux = `0.30/min`, Jeux = `0.20/min`, Utilitaires = `0.00/min`.
- Bonus quota : `+10` points accordés si tous les quotas sont respectés.

### Courbe exponentielle des niveaux (`LevelEngine`)
L'XP requis pour atteindre un niveau $n$ suit une courbe exponentielle :
$$\text{XP}_{\text{requis}}(n) = 100 \times (n - 1)^{1.5}$$
À chaque palier, un nouveau rang visuel est attribué (*Braise*, *Flamme*, *Aurore*, *Nébuleuse*, *Zénith*).

---

## 5. Couche Système & Fiabilité Arrière-Plan

1. **Mesure du temps par application (`UsageCollector`)** :
   Utilise `UsageStatsManager.queryEvents()`. Plutôt que d'estimer via un polling grossier, le système analyse les transitions réelles `ACTIVITY_RESUMED` et `ACTIVITY_PAUSED` / `SCREEN_NON_INTERACTIVE`.
2. **Détection des déverrouillages** :
   - *Temps réel* : `BroadcastReceiver` dynamique pour `Intent.ACTION_USER_PRESENT` dans le `TrackingService`.
   - *Rattrapage rétroactif* : Événements `KEYGUARD_HIDDEN` extraits de `UsageEvents` lors d'un réveil après mise en veille prolongée.
3. **Interception du lancement d'applications (`BlockerAccessibilityService`)** :
   Écoute `TYPE_WINDOW_STATE_CHANGED`. Dès qu'une application ciblée passe au premier plan alors que le quota est dépassé, l'activité de blocage [`BlockActivity`](file:///c:/Users/Champeley/Desktop/AppT/feature/blocker/src/main/kotlin/com/detox/feature/blocker/BlockActivity.kt) est lancée par-dessus.

---

## 6. Procédure de Déploiement Pas-à-Pas (Build APK & Install)

### Prérequis sur votre PC
1. **JDK 17** (ex: Eclipse Temurin JDK 17 ou OpenJDK 17).
2. **Android Studio** (Koala, Hedgehog ou plus récent) ou les outils Android SDK en ligne de commande.
3. Un câble USB pour relier votre téléphone, ou un transfert direct de fichier APK.

### Étape 1 : Ouvrir le projet
1. Lancez **Android Studio**.
2. Cliquez sur **Open** et sélectionnez le dossier :
   `c:\Users\Champeley\Desktop\AppT`
3. Laissez Gradle synchroniser les dépendances (Sync Project with Gradle Files).

### Étape 2 : Générer l'APK de Débogage / Release
Dans le terminal d'Android Studio ou dans PowerShell à la racine du projet :
```powershell
# Générer l'APK Debug
.\gradlew assembleDebug
```
L'APK généré se trouvera dans :
`app\build\outputs\apk\debug\app-debug.apk`

*(Vous pouvez aussi utiliser le menu Android Studio : **Build > Build Bundle(s) / APK(s) > Build APK(s)**)*.

### Étape 3 : Installer l'APK sur votre smartphone
**Option A : Via ADB (câble USB)**
1. Activez les **Options pour les développeurs** sur votre smartphone (appuyez 7 fois sur *Numéro de build* ou *Version MIUI/OS* dans les paramètres du téléphone).
2. Activez le **Débogage USB** (et sur Xiaomi, activez également **Débogage USB (paramètres de sécurité)**).
3. Branchez votre téléphone et exécutez :
```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

**Option B : Transfert direct (Sideload)**
Copiez le fichier `app-debug.apk` sur votre téléphone (via câble, Google Drive, Telegram, etc.) et appuyez dessus pour l'installer.

---

## 7. Guide Spécifique Xiaomi / HyperOS / MIUI

Les appareils Xiaomi intègrent des optimisations de batterie très agressives qui peuvent arrêter le service en arrière-plan ou bloquer les fonctionnalités d'accessibilité. Suivez impérativement ces 3 étapes lors de la première installation :

### 1. Lever les « Paramètres restreints » (Android 13+ / HyperOS)
Sur Android 13 et supérieur, toute application installée par APK voit son service d'accessibilité grisé par défaut :
1. Allez dans les **Paramètres du téléphone > Applications > Gérer les applications**.
2. Recherchez et appuyez sur **Detox**.
3. En haut à droite, appuyez sur les **trois petits points `⋮`**.
4. Sélectionnez **« Autoriser les paramètres restreints »**.
5. Validez avec votre empreinte ou code.

### 2. Activer le Démarrage automatique (Autostart)
1. Toujours sur la page d'informations de l'application **Detox**.
2. Activez l'option **« Démarrage automatique »** (Autostart).
3. *(Optionnel)* Dans les permissions secondaires, autorisez l'affichage de fenêtres contextuelles en arrière-plan.

### 3. Désactiver l'économiseur de batterie MIUI
1. Sur la page d'informations de **Detox**, faites défiler vers le bas jusqu'à **« Économiseur de batterie »**.
2. Sélectionnez **« Pas de restrictions »** (au lieu de *Économiseur MIUI recommandé*).

### 4. Activer les autorisations dans Detox
Ouvrez l'application Detox et utilisez l'écran **Diagnostic & Fiabilité** (`DiagnosticScreen`) intégré pour activer :
- L'accès aux données d'utilisation (`Usage Access`).
- Le service d'accessibilité `Detox`.

---

## 8. Évolution & Prochaines Étapes

- **Phase 4 (UI finale)** : Implémentation du shader AGSL fluide d'arrière-plan (`RuntimeShader` sur API 33+) pour donner une dynamique vivante au dashboard en fonction du score.
- **Export / Import JSON** : Connecter l'interface des réglages aux méthodes `exportConfigJson()` et `importConfigJson()` de [`ConfigRepository.kt`](file:///c:/Users/Champeley/Desktop/AppT/core/data/src/main/kotlin/com/detox/core/data/repository/ConfigRepository.kt) pour partager des presets de discipline entre utilisateurs.
- **Widgets Glance** : Création d'un widget minimaliste affichant le niveau, la flamme de streak et la jauge de déverrouillages sur le bureau du smartphone.
