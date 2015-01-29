package kz.greepto.gpen.editors.gpen.prop;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;

import kz.greepto.gpen.editors.gpen.action.Oper;
import kz.greepto.gpen.editors.gpen.action.OperModify;
import kz.greepto.gpen.editors.gpen.model.IdFigure;
import kz.greepto.gpen.editors.gpen.model.Label;
import kz.greepto.gpen.editors.gpen.model.Table;
import kz.greepto.gpen.util.Handler;
import kz.greepto.gpen.util.HandlerKiller;
import kz.greepto.gpen.util.HandlerList;

import org.testng.annotations.Test;

public class PropFactoryTest {
  
  public static class TestSceneWorker implements SceneWorker {
    public final HandlerList handlerList = new HandlerList();
    
    @Override
    public String takeId(Object object) {
      return "asd";
    }
    
    @Override
    public void applyOper(Oper action) {
      try {
        sendActionInner(action);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    Object objectToSet;
    
    private void sendActionInner(Oper action) throws Exception {
      if (action instanceof OperModify) {
        OperModify am = (OperModify)action;
        Field fNewValue = am.getClass().getDeclaredField("newValue");
        fNewValue.setAccessible(true);
        Field fSetter = am.getClass().getDeclaredField("setter");
        fSetter.setAccessible(true);
        
        ValueSetter setter = (ValueSetter)fSetter.get(action);
        
        Object newValue = fNewValue.get(action);
        setter.setValue(objectToSet, newValue);
        
        return;
      }
      System.out.println("Sended: " + action);
    }
    
    @Override
    public HandlerKiller addChangeHandler(Handler handler) {
      return handlerList.add(handler);
    }
    
    @Override
    public IdFigure findByIdOrDie(String id) {
      throw new UnsupportedOperationException("findByIdOrDie");
    }
    
    @Override
    public List<String> all() {
      throw new UnsupportedOperationException("all");
    }
    
  }
  
  public static class Asd {
    public int nomer = 890;
    
    public String getWorld() {
      return "JI";
    }
  }
  
  @Test
  public void parseObject_01() throws Exception {
    Asd asd = new Asd();
    
    TestSceneWorker sceneWorker = new TestSceneWorker();
    
    PropList list = PropFactory.parseObject(asd, sceneWorker);
    
    System.out.println("---------------------------------------------");
    for (PropAccessor pa : list) {
      System.out.println(pa);
    }
    System.out.println("---------------------------------------------");
    
    Object value = list.byName("nomer").getValue();
    System.out.println("value = " + value);
    
    assertThat(1);
  }
  
  @Test
  public void parseObject_02() throws Exception {
    Label asd = new Label("HI");
    
    TestSceneWorker sceneWorker = new TestSceneWorker();
    
    PropList list = PropFactory.parseObject(asd, sceneWorker);
    
    System.out.println("---------------------------------------------");
    for (PropAccessor pa : list) {
      System.out.println(pa);
      if ("id".equals(pa.getName())) {
        sceneWorker.objectToSet = asd;
        pa.setValue("fff");
        
        System.out.println("id = " + pa.getValue());
      }
    }
    System.out.println("---------------------------------------------");
    
    Object value = list.byName("text").getValue();
    System.out.println("value = " + value);
    
    assertThat(1);
  }
  
  @Test
  public void parseObject_03() throws Exception {
    Table tab = new Table("WOW-wow");
    
    TestSceneWorker sceneWorker = new TestSceneWorker();
    sceneWorker.objectToSet = tab;
    
    PropList list = PropFactory.parseObject(tab, sceneWorker);
    
    System.out.println("---------------------------------------------");
    for (PropAccessor pa : list) {
      System.out.println(pa);
    }
    System.out.println("---------------------------------------------");
    
    Object value = list.byName("content").getValue();
    System.out.println("value = " + value);
    
    list.byName("content").setValue("wwwwwwwwww");
    
    assertThat(1);
  }
}