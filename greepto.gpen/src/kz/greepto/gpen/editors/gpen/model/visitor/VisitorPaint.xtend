package kz.greepto.gpen.editors.gpen.model.visitor

import org.eclipse.swt.graphics.GC
import kz.greepto.gpen.editors.gpen.model.Scene
import kz.greepto.gpen.editors.gpen.model.Label
import kz.greepto.gpen.editors.gpen.model.Combo
import kz.greepto.gpen.editors.gpen.model.Button

class VisitorPaint implements FigureVisitor<Void> {

  final GC gc

  new(GC gc) {
    this.gc = gc
  }

  override visitScene(Scene scene) {
    scene.list.forEach[visit(this)]
    null
  }

  override visitLabel(Label label) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub")
  }

  override visitCombo(Combo combo) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub")
  }

  override visitButton(Button button) {
    throw new UnsupportedOperationException("TODO: auto-generated method stub")
  }

}
