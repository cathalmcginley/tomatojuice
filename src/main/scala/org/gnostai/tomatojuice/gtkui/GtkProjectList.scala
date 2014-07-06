package org.gnostai.tomatojuice.gtkui

import org.gnome.gtk._
import org.gnome.gdk
import org.gnostai.tomatojuice.core.CoreDomainModule

trait WidgetManager {
  protected[gtkui] def widget: Widget
}


class GtkProjectList extends WidgetManager 
  with CoreDomainModule
  with GtkUIFacadeModule {

  private val projectPane = new VBox(false, 0)

  private val navLabel = new Label("Projects")
  private val listModel = buildListModel
  private val mainScrollpane = buildProjectScrollpane
  
  arrangeWidgets()

  
  def displayProjects(projects: Iterable[Project]) {
    safely {
      listModel.clear()
      projects.map(addProject)
    }
  }
  
  
  private def buildProjectScrollpane = {    
    val scrollpane = new ScrolledWindow
    val treeview = buildTreeView //new TreeView
    scrollpane.add(treeview)
    scrollpane
  }

  object Columns {
    
    val imgData = new ImageDataFromFile("/home/cathal/software/projects/alexandria/data/alexandria/icons/library_small.png").data
    val BookIcon = new gdk.Pixbuf(imgData)
    
    
    val Icon = new DataColumnPixbuf
    val ProjectName = new DataColumnString
    val CompletedPomodoros = new DataColumnInteger
    val UserManualSort = new DataColumnString // DataColumnInteger
    
    def toArray = Array[DataColumn](Icon, ProjectName, CompletedPomodoros, UserManualSort)
  }
  
  private def addProject(project: Project) {
    val row = listModel.appendRow()
    listModel.setValue(row, Columns.Icon, Columns.BookIcon)  // TODO use icon from database
    listModel.setValue(row, Columns.ProjectName, project.name)
    listModel.setValue(row, Columns.CompletedPomodoros, 0)
    listModel.setValue(row, Columns.UserManualSort, project.name)
  }
  
  private def buildListModel = new ListStore(Columns.toArray)
  
  private def buildTreeView = {    
    val view = new TreeView(listModel);
    view.setHeadersVisible(false)

    val iconColumn = view.appendColumn()
    val iconRenderer = new CellRendererPixbuf(iconColumn)
    iconRenderer.setPixbuf(Columns.Icon)
        
    val projectNameColumn = view.appendColumn()
    projectNameColumn.setTitle("Name")
    val nameRenderer = new CellRendererText(projectNameColumn)
    nameRenderer.setMarkup(Columns.ProjectName)
    projectNameColumn.setSortColumn(Columns.UserManualSort)
    projectNameColumn.emitClicked()

    view.getSelection().connect(new TreeSelection.Changed() {
      def onChanged(source: TreeSelection) {
        val row = source.getSelected()
        val name = listModel.getValue(row, Columns.ProjectName)
        println("Selected project '" + name + "'")  // TODO remove println
      }
    })

    view
  }

  private def arrangeWidgets() {
    navLabel.setAlignment(0, 0)
    navLabel.setPadding(6, 2) // was 6,6,
    projectPane.packStart(navLabel, false, false, 0) // was 6
    projectPane.packStart(mainScrollpane, true, true, 0)
  }
  

  override protected[gtkui] def widget: Widget = projectPane
}