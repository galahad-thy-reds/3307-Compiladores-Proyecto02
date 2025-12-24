# Validador HTML/JavaScript

Analizador estático para archivos HTML y JavaScript que valida el código según 8 requerimientos específicos. Desarrollado utilizando un enfoque de Árbol de Sintaxis Abstracta (AST) con un parser de máquina de estados.

**Curso**: Compiladores (3307) - UNED  
**Proyecto**: Proyecto 2  
**Valor**: 2%  
**Autor**: eduardo

## Descripción General

Este proyecto implementa un validador que lee archivos HTML que contienen JavaScript embebido, los analiza construyendo un Árbol de Sintaxis Abstracta (AST), y valida el código según 8 requerimientos específicos. El validador genera un archivo de reporte de errores numerado (.txt) con todos los errores detectados.

Según las especificaciones del proyecto, el programa debe:
- Leer un archivo con extensión `.html` que contendrá código HTML y JavaScript
- Realizar una copia del archivo original con extensión `.txt`
- Enumerar las líneas en formato de 4 dígitos (0001, 0002, etc.)
- Evidenciar los errores haciendo referencia a la línea correspondiente

## Temas de Estudio Aplicados

- **Tema 4**: Análisis sintáctico
- **Tema 5**: Traducción orientada por la sintaxis
- **Tema 6**: Generación de código intermedio

## Objetivo

Aplicar los conocimientos adquiridos en el curso, abordando cada uno de los temas mencionados, estableciendo conexiones entre los conceptos hacia la programación, con el objetivo de profundizar en las ideas fundamentales relacionadas con un compilador.

## Características

- **Análisis Léxico**: Tokeniza código HTML y JavaScript
- **Análisis Sintáctico**: Construye un AST completo usando un parser de máquina de estados
- **8 Reglas de Validación**: Implementa todos los requerimientos de la especificación del proyecto
- **Reporte de Errores**: Genera archivos de salida numerados con mensajes de error detallados
- **Sin Dependencias Externas**: Implementación Java autocontenida

## Requisitos del Sistema

- **Java 21** o superior
- **NetBeans IDE** (para desarrollo, modo carácter)
- **No se permiten librerías de terceros**: El proyecto debe tener independencia física en la ejecución, lo que quiere decir que el programa debe poder ser ejecutado en cualquier máquina y en cualquier carpeta

## Estructura del Proyecto

```
src/
├── Main.java                    # Punto de entrada
├── ValidatorEngine.java         # Orquestador principal
├── lexer/                       # Tokenización
│   ├── Lexer.java
│   ├── Token.java
│   └── TokenType.java
├── parser/                      # Construcción del AST
│   ├── Parser.java
│   └── ParserState.java
├── ast/                         # Definiciones de nodos AST
│   ├── Node.java
│   ├── html/                    # Nodos HTML
│   │   ├── DocumentNode.java
│   │   ├── TagNode.java
│   │   ├── AttributeNode.java
│   │   └── TextNode.java
│   └── js/                      # Nodos JavaScript
│       ├── ScriptNode.java
│       ├── FunctionNode.java
│       ├── VariableNode.java
│       ├── ConstantNode.java
│       ├── AssignmentNode.java
│       ├── ExpressionNode.java
│       ├── CallNode.java
│       └── IdentifierNode.java
├── validators/                  # Reglas de validación
│   ├── Validator.java
│   ├── HtmlStructureValidator.java
│   ├── IdentifierValidator.java
│   ├── ConstantValidator.java
│   ├── AssignmentValidator.java
│   ├── FunctionValidator.java
│   ├── DataInputValidator.java
│   ├── DataOutputValidator.java
│   └── HtmlElementValidator.java
├── errors/                      # Gestión de errores
│   ├── Error.java
│   ├── ErrorCollector.java
│   └── ErrorReporter.java
└── utils/                       # Utilidades
    ├── FileUtils.java
    └── ReservedWords.java
```

## Arquitectura

El sistema sigue una arquitectura de pipeline:

```
Archivo HTML → Lexer → Tokens → Parser → AST → Validadores → Recolector de Errores → Generador de Reporte → Archivo TXT
```

### Componentes

1. **Lexer**: Tokeniza HTML y JavaScript en un flujo de tokens
2. **Parser**: Máquina de estados que construye un AST a partir de tokens
3. **AST**: Representación completa de la estructura del documento
4. **Validadores**: 8 validadores independientes que verifican reglas específicas
5. **Error Collector**: Recolección centralizada de errores
6. **Error Reporter**: Genera archivo de salida numerado con errores

## Compilación y Ejecución

### Compilar

```bash
javac -d build/classes -sourcepath src src/Main.java
```

O usar NetBeans:
- Abrir el proyecto en NetBeans
- Compilar Proyecto (F11)

### Ejecutar

```bash
java -cp build/classes Main <archivo.html>
```

Ejemplo:
```bash
java -cp build/classes Main test/Bueno1.html
```

Esto generará `test/Bueno1.txt` con líneas numeradas y anotaciones de errores.

### Ejecutar Todas las Pruebas

```bash
java -cp build/classes TestRunner test
```

Esto ejecuta la validación en todos los archivos HTML del directorio `test/` y proporciona un resumen.

## Requerimientos de Validación

El validador verifica 8 requerimientos según la especificación del proyecto:

### Requerimiento #1: Archivo de Errores (5 puntos)

Es necesario crear una copia del archivo original, pero con extensión `.txt`. Este archivo contendrá el contenido del archivo `.html` pero con las líneas enumeradas en formato de 4 dígitos (por ejemplo, 0001) y el contenido de la línea. En este archivo se deben especificar los errores indicando la línea donde está el error, puede ser debajo de la línea del error, haciendo referencia a un número de error y la línea del error, con la respectiva descripción del error. Es importante indicar que se deben de identificar todos los errores y en una línea separada.

### Requerimiento #2: Identificadores (10 puntos)

En JavaScript, los identificadores son nombres que se utilizan para nombrar variables, funciones y otros elementos del programa. Para que un identificador sea válido y cumpla con las convenciones de nomenclatura, debe seguir ciertos patrones y reglas:

- Deben comenzar con una letra (a-z, A-Z) o un guion bajo (_) o un carácter especial del sistema de escritura del lenguaje (como los caracteres Unicode que representan letras de otros idiomas)
- Después del primer carácter, se pueden usar letras, dígitos (0-9), guiones bajos o caracteres especiales del sistema de escritura
- No se pueden usar espacios en blanco ni caracteres especiales como "-", "+", "*", "/", etc.
- JavaScript es sensible a mayúsculas y minúsculas, por lo que "miVariable" y "mivariable" se considerarán identificadores diferentes
- No se pueden utilizar palabras reservadas de JavaScript como identificadores
- Los identificadores van después de los comandos `let` o bien `var`

### Requerimiento #3: Constantes (10 puntos)

Para definir una constante en JavaScript, debes seguir algunas reglas y convenciones:

- El nombre de las constantes aplica las mismas reglas de los **IDENTIFICADORES**
- Debes utilizar la palabra clave **`const`** para declarar una constante
- Debes asignar un valor a la constante en el mismo momento en que la declaras. No puedes declarar una constante sin asignarle un valor
- Una vez que asignas un valor a una constante, no puedes cambiar ese valor en el futuro. Intentar hacerlo generará un error
- Las constantes deben declararse antes de ser utilizadas, y no pueden declararse después de las variables (`var`, `let`) en el mismo ámbito. Esto se debe a las reglas de alcance (scope) en JavaScript

### Requerimiento #4: Asignación (15 puntos)

En JavaScript, puedes asignar valores a una variable utilizando varios operadores de asignación:

- **Operador de Asignación (=)**: Este es el operador de asignación más básico y se utiliza para asignar un valor a una variable
  - Ejemplo: `let numero = 42;`
- **Operadores de Asignación Compuesta**: Estos operadores realizan una operación y luego asignan el resultado a la variable
  - Ejemplo: `total += 5;` (suma y asignación)
  - Ejemplo: `saldo -= 30;` (resta y asignación)
- **Operador de Asignación en Cadena (=)**: Puedes asignar valores a múltiples variables en una sola línea
  - Ejemplo: `let a, b, c; a = b = c = 42;`

Se puede asumir que todo lo que viene a la derecha del último igual en caso que tenga más de uno es correcto. Pero se deben validar que todos los tipos de datos de las variables que aparecen a la derecha del igual coinciden con el tipo de datos de la variable que aparece a la izquierda del igual.

### Requerimiento #5: Definir Funciones (15 puntos)

Para definir una función en JavaScript, debes seguir ciertas reglas y convenciones:

- Puedes declarar una función en JavaScript utilizando la palabra clave `function` seguida por el nombre de la función y un conjunto de paréntesis
- Después de los paréntesis, se utiliza un bloque de código en llaves `{ }` para definir el cuerpo de la función
- Debe haber al menos una línea de comando dentro del bloque del cuerpo de la función
- Los nombres de las funciones deben seguir las mismas reglas de nomenclatura que las variables
- Se debe de verificar que los parámetros que recibe la función están correctos y deben de estar contenidos por la apertura y cierre de las etiquetas `<script></script>`

### Requerimiento #6: Entrada de Datos (10 puntos)

Se debe validar que el comando para entrada de datos siga esta sintaxis:

- `document.getElementById("id").value`

El valor ID que está dentro de las comillas corresponde al ID del control HTML que obtiene el valor determinado por el usuario, se debe validar que ese control esté definido.

Ejemplo: `const nombre = document.getElementById("nombre").value;`

### Requerimiento #7: Salida de Datos (10 puntos)

Se debe validar que el comando para salida de datos siga esta sintaxis:

- `document.getElementById("id del control").innerHTML = resultado;`

Lo que se indica entre comillas dobles corresponde al ID del control donde se va a mostrar la información. Lo que está a la derecha del igual puede asumirse que está correcto. Debe de seguir la sintaxis indicada, por lo tanto, debe existir el id del control.

### Requerimiento #8: Estructura HTML (25 puntos)

Todas las etiquetas tienen una etiqueta de apertura y otra etiqueta de cierre a excepción de la etiqueta `<!DOCTYPE html>`.

- La etiqueta `<!DOCTYPE html>` debe estar determinada de la manera indicada, cualquier omisión de su conformación es un error
- La etiqueta `<!DOCTYPE html>` siempre estará al inicio del código HTML
- Las etiquetas y el orden correcto de la posición de ellas son la siguiente estructura:

```
<!DOCTYPE html>
<html>
<head>
...
</head>
<body>
...
</body>
</html>
```

## Ejemplo de Uso

### Archivo de Entrada (test.html)
```html
<!DOCTYPE html>
<html>
<head>
    <title>Prueba</title>
</head>
<body>
    <input id="nombre" type="text">
    <script>
        const nombre = document.getElementById("nombre").value;
        function prueba() {
            let x = 5;
        }
    </script>
</body>
</html>
```

### Archivo de Salida (test.txt)
```
0001 <!DOCTYPE html>
0002 <html>
0003 <head>
0004     <title>Prueba</title>
0005 </head>
0006 <body>
0007     <input id="nombre" type="text">
0008     <script>
0009         const nombre = document.getElementById("nombre").value;
0010         function prueba() {
0011             let x = 5;
0012         }
0013     </script>
0014 </body>
0015 </html>
```

Si hay errores, se listarán debajo de las líneas correspondientes.

## Pruebas

El proyecto incluye archivos de prueba en el directorio `test/`:

- **Bueno1.html, Bueno2.html**: Ejemplos buenos (deben tener errores mínimos o ninguno)
- **Malo1.html, Malo2.html, Malo3.html**: Ejemplos malos con errores intencionales
- **Calculadora de Edad.html**: Ejemplo del PDF del proyecto
- **Ejemplo JavaScript.html**: Ejemplo del PDF del proyecto

Ejecutar todas las pruebas:
```bash
java -cp build/classes TestRunner test
```

## Decisiones de Diseño Clave

1. **Parser de Máquina de Estados**: Maneja el cambio entre modos HTML/JS de manera limpia
2. **AST Completo**: Representación completa permite que todos los validadores trabajen independientemente
3. **Error Collector**: Gestión centralizada de errores antes del reporte
4. **Seguimiento de Números de Línea**: Cada nodo AST almacena su número de línea para el reporte de errores
5. **Validadores Modulares**: Cada requerimiento tiene su propia clase validadora

## Correcciones de Errores Aplicadas

### Problema 1: Detección de Etiqueta HTML Faltante
Se corrigió el parser para identificar correctamente la etiqueta `<html>` incluso cuando aparece en la misma línea que otras etiquetas como `<html><head>`.

### Problema 2: Análisis de Asignación de Constantes
Se corrigió el parser para manejar correctamente expresiones complejas como `new Date()` y encadenamiento de métodos como `document.getElementById("id").value` como asignaciones válidas de constantes.

### Problema 3: Errores de Operador de Asignación Inválido
Se corrigió el AssignmentValidator para validar solo operadores de asignación reales (=, +=, -=, *=, /=, %=) y no marcar operadores de comparación (<, >, <=, >=, ==, !=) u operadores lógicos (&&, ||) como inválidos.

## Limitaciones

- Análisis de expresiones simplificado (maneja casos comunes pero no toda la sintaxis de JavaScript)
- Verificación de tipos básica (asume que el valor más a la derecha es correcto para asignaciones en cadena)
- El análisis de atributos HTML está simplificado (maneja casos comunes)

## Notas Importantes del Proyecto

- El proyecto debe estar desarrollado en **NetBeans** que es la herramienta oficial de la asignatura
- El programa debe ser **modular**, utilizando de la mejor manera funciones definidas por usted
- Los trabajos deben realizarse en forma **individual**
- Dentro del código del programa debe de indicar la **documentación** que explique cómo fue realizado el programa
- Si utiliza código de algún ejemplo del libro, o de otra fuente que no sea de su autoría, debe de indicarlo
- El programa debe tener **independencia física** en la ejecución (debe poder ejecutarse en cualquier máquina y en cualquier carpeta)
- **No se permite el uso de librerías de terceros**

## Rúbrica de Evaluación

| Criterio | Cumple de manera excelente | Cumple medianamente | Cumple en contenido y formato, pero los aportes no son significantes | No cumple o no presenta |
|----------|---------------------------|---------------------|----------------------------------------------------------------------|------------------------|
| Punto #1 | 5 | 3 | 2 | 1 |
| Punto #2 | 10 | 5 | 3 | 1 |
| Punto #3 | 10 | 5 | 3 | 1 |
| Punto #4 | 15 | 10 | 5 | 1 |
| Punto #5 | 15 | 10 | 5 | 1 |
| Punto #6 | 10 | 5 | 3 | 1 |
| Punto #7 | 10 | 5 | 3 | 1 |
| Punto #8 | 25 | 15 | 10 | 1 |
| **TOTAL** | **100** | | | |

## Licencia

Este proyecto es parte de una asignación académica del curso Compiladores (3307) de la UNED.

## Autor

eduardo
