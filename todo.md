# 📋 Sistema de Dificultad Dinámica – Checklist de Desarrollo

## 🧭 General

* [ ] Crear archivo de configuración (`difficulty_scaling.json`)
* [ ] Implementar almacenamiento persistente de niveles (por jugador)
* [ ] Definir función de cálculo global de dificultad

    * [ ] Ponderación por categorías
    * [ ] Curva de escalado no lineal
    * [ ] Sincronización con tick del servidor
* [ ] Añadir comando de depuración (`/diffstats`) para ver estado actual del jugador
* [ ] Crear interfaz visual (opcional) para monitorear niveles

---

## 🌅 Nivel de Supervivencia

**Objetivo:** medir la capacidad de mantenerte con vida a lo largo de los días.

### Lógica base

* [ ] Contador de días sobrevividos sin morir
* [ ] Registro de daño recibido vs daño potencial
* [ ] Promedio de salud mantenida

### Efectos dinámicos

* [ ] Incrementar daño base y velocidad de mobs hostiles
* [ ] Aumentar frecuencia de spawns nocturnos
* [ ] Reducir duración de inmunidad post-daño
* [ ] Añadir posibilidad de mobs “veteranos” (mayor IA o resistencia)
* [ ] Ajuste gradual tras cada muerte (reducción parcial del nivel)

### Balance

* [ ] Aplicar enfriamiento de dificultad en zonas seguras (base, cama)
* [ ] Configurar límites máximos/minimos del escalado

---

## ⚔️ Nivel de Combate

### 🪓 Subnivel: Melee

**Factores**

* [ ] Registrar golpes/muertes cuerpo a cuerpo
* [ ] Registrar daño total infligido
* [ ] Actualizar nivel por ratio de éxito

**Efectos**

* [ ] Incrementar cantidad de enemigos arqueros
* [ ] Aumentar resistencia al retroceso de mobs melee
* [ ] Incrementar probabilidad de mobs con armadura
* [ ] Habilitar efectos de velocidad o fuerza en mobs
* [ ] Ajustar IA: mobs que flanquean o evaden

**Balance**

* [ ] Reducción leve del incremento si jugador cambia a combate a distancia

---

### 🏹 Subnivel: Distancia

**Factores**

* [ ] Contar muertes y golpes a distancia
* [ ] Medir precisión (impactos / disparos totales)
* [ ] Ajustar nivel según frecuencia de uso del arco

**Efectos**

* [ ] Incrementar mobs melee que acortan distancia
* [ ] Reducir tiempo de carga de esqueletos
* [ ] Introducir mobs con resistencia a flechas
* [ ] Incrementar obstáculos naturales o cobertura

**Balance**

* [ ] Evitar sobrepoblación de mobs ranged
* [ ] Bonificación al jugador por alternar estilos de combate

---

## ⛏️ Nivel de Obtención de Recursos

**Factores**

* [ ] Calcular valor total de recursos en inventario y cofres
* [ ] Medir frecuencia de minería o recolección
* [ ] Analizar nivel de equipamiento

**Efectos**

* [ ] Incrementar spawns en minas y cuevas
* [ ] Implementar emboscadas al minar minerales raros
* [ ] Reducir drop rates de materiales valiosos
* [ ] Introducir trampas naturales (lava oculta, derrumbes)
* [ ] Ajustar resistencia de mobs según equipamiento detectado

**Balance**

* [ ] Reducción parcial del nivel si jugador pierde inventario
* [ ] Bonificación por exploración (no solo acumulación)

---

## 🌍 Nivel Ambiental (Opcional)

**Factores**

* [ ] Tiempo en biomas hostiles
* [ ] Exploración de estructuras peligrosas

**Efectos**

* [ ] Aumento de clima extremo y eventos naturales
* [ ] Incremento de trampas y mobs especiales en dungeons
* [ ] Ajuste del ciclo día/noche según progresión

**Balance**

* [ ] Zonas seguras sin escalado
* [ ] Regeneración pasiva al volver a áreas seguras

---

## ⚙️ Control Global

* [ ] Implementar función de cálculo de dificultad total

  ```
  Dificultad = (Supervivencia * 0.4) + (Combate * 0.4) + (Recursos * 0.2)
  ```
* [ ] Guardar valores y escalado por jugador
* [ ] Configurar eventos de recalibración (cada X días, al morir, etc.)
* [ ] Implementar modo debug con valores visibles en HUD

