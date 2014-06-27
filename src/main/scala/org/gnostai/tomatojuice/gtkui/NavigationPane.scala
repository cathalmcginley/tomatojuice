package org.gnostai.tomatojuice.gtkui

import org.gnome.gtk._
import org.gnome.gdk.Pixbuf

trait WidgetManager {
  protected[gtkui] def widget: Widget
}


class NavigationPane extends WidgetManager {

  private val navPane = new VBox(false, 0)

  private val navLabel = new Label("Projects")
  private val mainNotebook = buildNavNotebook
  

  arrangeWidgets()
  

  private def buildNavNotebook = {
    val notebook = new Notebook
    notebook.setShowTabs(false)
    val scrollpane = new ScrolledWindow
    val treeview = buildTreeView //new TreeView
    scrollpane.add(treeview)
    notebook.insertPage(scrollpane, new Label(""), 0)
    notebook.setCurrentPage(0)
    notebook
  }



  private def buildTreeView = {
    //val buildTreeview: view
    //val model: ListStore = null
    var row: TreeIter = null
    var renderer: CellRendererText = null
    //var vertical: TreeViewColumn = null

    // ------------------------------------------------

    val iconCol = new DataColumnPixbuf
    val collectionName = new DataColumnString
    val collectionSortName = new DataColumnString
    //val elevationFormatted = new DataColumnString
    val userManualSort = new DataColumnInteger
    //val accessibleByTrain = new DataColumnBoolean

    val model = new ListStore(Array[DataColumn](iconCol, collectionName, collectionSortName,
      userManualSort))

    val imgData = new ImageData("/home/cathal/software/projects/alexandria/data/alexandria/icons/library_small.png").data
    val BOOK_ICON = new Pixbuf(imgData)
    
    row = model.appendRow();
    model.setValue(row, iconCol, BOOK_ICON)
    model.setValue(row, collectionName, "The Alpha");
    model.setValue(row, collectionSortName, "Alpha");
    model.setValue(row, userManualSort, 1);
    

    row = model.appendRow(); 
    model.setValue(row, iconCol, BOOK_ICON)
    model.setValue(row, collectionName, "The Beta");
    model.setValue(row, collectionSortName, "Beta");
    model.setValue(row, userManualSort, 2);

    row = model.appendRow();
    model.setValue(row, iconCol, BOOK_ICON)
    model.setValue(row, collectionName, "The Gamma");
    model.setValue(row, collectionSortName, "Gamma");
    model.setValue(row, userManualSort, 3);

    row = model.appendRow();
    model.setValue(row, iconCol, BOOK_ICON)
    model.setValue(row, collectionName, "The Delta");
    model.setValue(row, collectionSortName, "Delta");
    model.setValue(row, userManualSort, 4);

    val view = new TreeView(model);
    view.setHeadersVisible(false)

    
    val iconColumnView = view.appendColumn()
    val iconRenderer = new CellRendererPixbuf(iconColumnView)
    iconRenderer.setPixbuf(iconCol)
    
    
    val collectionNameCol = view.appendColumn();
    collectionNameCol.setTitle("Name");
    //vertical.set
    renderer = new CellRendererText(collectionNameCol);
    renderer.setMarkup(collectionName);

    //    vertical = view.appendColumn();
    //    vertical.setTitle("Nearest town");
    //    renderer = new CellRendererText(vertical);
    //    renderer.setText(collectionSortName);
    //    renderer.setAlignment(0.0f, 0.0f);
    //    vertical.setExpand(true);
    //
    //    vertical = view.appendColumn();
    //    vertical.setTitle("Elevation");
    //    renderer = new CellRendererText(vertical);
    //    renderer.setText(elevationFormatted);
    //    renderer.setAlignment(0.0f, 0.0f);

    collectionNameCol.setSortColumn(userManualSort);
    collectionNameCol.emitClicked();

    view.getSelection().connect(new TreeSelection.Changed() {
      def onChanged(source: TreeSelection) {

        val row = source.getSelected()
        val name = model.getValue(row, collectionName)
        val sortName = model.getValue(row, collectionSortName)
        println("Selected Collection '" + name + "'")

      }
    })

    view
  }

  private def arrangeWidgets() {
    navLabel.setAlignment(0, 0)
    navLabel.setPadding(6, 2) // was 6,6,
    navPane.packStart(navLabel, false, false, 0) // was 6
    navPane.packStart(mainNotebook, true, true, 0)
  }
  

  override protected[gtkui] def widget: Widget = navPane
}

import java.io.File
import java.io.FileInputStream

class ImageData(val fileName: String) {

  private def readFileData: Array[Byte] = {
    //    val source = scala.io.Source.fromFile(fileName)
    //    val byteArray = source.map(_.toByte).toArray
    //    println(byteArray.length)
    //    source.close()
    //    byteArray

    val file = new File(fileName)
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
    return bytes

    //val bis = new BufferedInputStream(new FileInputStream(fileName))
    //val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    //bArray
  }

  lazy val data: Array[Byte] = readFileData
}

