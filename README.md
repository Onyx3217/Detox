# Project Detox - Environment & Build Guide

## Architecture
Le projet utilise une architecture multi-modules moderne :
- `:core:model` : Modèles de données purs (Kotlin pur + kotlinx.serialization).
- `:core:domain` : Moteur de score (`ScoreEngine`), progression de niveau (`LevelEngine`), reconstitution de sessions (`SessionRebuilder`) et politique de blocage (`BlockPolicyEvaluator`).
- `:core:designsystem` : Composants graphiques Compose (`LevelProgressRing`, palette de thèmes sombre/braise néon).
- `:core:data` : Entités Room et persistance DataStore.
- `:app` : Point d'entrée Android (`MainActivity`, Compose Dashboard).

## Prérequis pour le build local
1. **JDK 17 ou supérieur** (requis pour Gradle 8+ et Kotlin 1.9+).
2. **Android Studio** (Hedgehog ou version ultérieure) avec le SDK Android 34.
3. Pour tester dans Android Studio : Ouvrir simplement le dossier `c:\Users\Champeley\Desktop\AppT`.
