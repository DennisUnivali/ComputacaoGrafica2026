# Aula: VBO e OBJ com LWJGL 3 e Maven

Requisitos: JDK 17+, Maven 3.9+ e driver com suporte a OpenGL 2.1 (compatibility profile).

## Executar

Na raiz do projeto:

```bash
mvn compile exec:java
```

Para carregar outro arquivo OBJ:

```bash
mvn compile exec:java -Dexec.args="caminho/para/modelo.obj"
```

No Windows, também é possível abrir o projeto como **Maven Project** no IntelliJ ou Eclipse e executar `aula.Main`.

No macOS, a janela GLFW pode exigir execução com `-XstartOnFirstThread`. Se necessário, rode pela IDE com essa opção na JVM (o `exec:java` não cria outra JVM para aplicar esse argumento).

## Controles

- Segure `W` para wireframe.
- `ESC` fecha a janela.

## Objetivos didáticos

O código usa três VBOs (`positions`, `normals`, `uvs`) e renderiza com o pipeline de compatibilidade, sem shaders e sem VAO. O próximo passo é migrar para VAO, `glVertexAttribPointer` e GLSL.

O parser OBJ suporta `v`, `vt`, `vn`, `f`, índices positivos/negativos e triangulação em leque (para faces convexas). Não carrega `.mtl`, imagens de textura ou smoothing groups. Normais ausentes recebem valor padrão. Faces côncavas precisam ser trianguladas previamente. O VBO de UVs existe, mas não há texturização nesta aula.

Perfis Maven para natives: Windows x64/ARM64, Linux x64/ARM64 e macOS Intel/Apple Silicon.
