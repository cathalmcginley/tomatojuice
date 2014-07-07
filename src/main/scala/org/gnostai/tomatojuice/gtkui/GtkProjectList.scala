package org.gnostai.tomatojuice.gtkui

import org.gnome.gtk._
import org.gnome.gdk
import org.gnostai.tomatojuice.core.CoreDomainModule

trait WidgetManager {
  protected[gtkui] def widget: Widget
}

trait GtkProjectListModule extends CoreDomainModule
  with GtkUIFacadeModule {

  class GtkProjectList extends WidgetManager {

    private val projectPane = new VBox(false, 0)

    private val navLabel = new Label("Projects")
    private val listModel = buildListModel
    private val mainScrollpane = buildProjectScrollpane

    private var treeview: TreeView = _

    arrangeWidgets()

    def displayProjects(projects: Iterable[Project]) {
      safely {
        listModel.clear()
        projects.map(addProject)
        val projList = projects.toSeq
        if (!projList.isEmpty) {
          val sorted = projList.sortBy(p => p.id)
          val firstId = sorted.head.id.get
          var iter = listModel.getIterFirst()
          if (listModel.getValue(iter, Columns.Id) == firstId) {
            treeview.getSelection().selectRow(iter)
          }
          while (iter.iterNext()) {
            // copied from above
            if (listModel.getValue(iter, Columns.Id) == firstId) {
              treeview.getSelection().selectRow(iter)
            }
          }

        }
      }
    }

    def getSelectedProject: Option[Project] = {
      val selection = treeview.getSelection()
      if (selection != null) {
        val iter = selection.getSelected()
        if (iter != null) {
          println(">> " + iter)
          val projectName = listModel.getValue(iter, Columns.ProjectName)
          println("project name: " + projectName)
          val projectId = listModel.getValue(iter, Columns.Id)
          Some(Project(Some(projectId), projectName, "-", None))
        } else {
          println("iter null:: " + iter)
          None
        }
      } else {
        println("treeview selection null:: " + selection)
        None
      }

    }

    private def buildProjectScrollpane = {
      val scrollpane = new ScrolledWindow
      treeview = buildTreeView //new TreeView
      scrollpane.add(treeview)
      scrollpane
    }

    def isDefault(project: Project): Boolean = {
      project.id.get == 0
    }

    object Columns {

      val imgData = new ImageDataFromFile("src/main/resources/icons/palatina.png").data

      val Id = new DataColumnInteger
      val PalatinaIcon = new gdk.Pixbuf(imgData)
      val BlankIcon = new gdk.Pixbuf(EmbeddedPngIcons.Transparent)

      val Icon = new DataColumnPixbuf
      val ProjectName = new DataColumnString
      val CompletedPomodoros = new DataColumnInteger
      val UserManualSort = new DataColumnString // DataColumnInteger

      def toArray = Array[DataColumn](Id, Icon, ProjectName, CompletedPomodoros, UserManualSort)
    }

    private def addProject(project: Project) {
      if (isDefault(project)) {
        val row = listModel.appendRow()
        listModel.setValue(row, Columns.Id, project.id.get)
        listModel.setValue(row, Columns.Icon, Columns.BlankIcon) // TODO use icon from database
        listModel.setValue(row, Columns.ProjectName, "\n<small><i>No Project</i></small>\n")
        listModel.setValue(row, Columns.CompletedPomodoros, 0)
        listModel.setValue(row, Columns.UserManualSort, "00")
      } else {
        val row = listModel.appendRow()
        listModel.setValue(row, Columns.Id, project.id.get)
        listModel.setValue(row, Columns.Icon, Columns.PalatinaIcon) // TODO use icon from database
        listModel.setValue(row, Columns.ProjectName, project.name)
        listModel.setValue(row, Columns.CompletedPomodoros, 0)
        listModel.setValue(row, Columns.UserManualSort, project.name)
      }
    }

    private def buildListModel = new ListStore(Columns.toArray)

    private def buildTreeView = {
      val view = new TreeView(listModel)
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
          if (row != null) {
            val name = listModel.getValue(row, Columns.ProjectName)
            println("Selected project '" + name + "'") // TODO remove println
          }
        }
      })
      view.getSelection().setMode(SelectionMode.BROWSE) // exactly one item is always selected

      view
    }

    private def arrangeWidgets() {
      mainScrollpane.setSizeRequest(150, 400)
      navLabel.setAlignment(0, 0)
      navLabel.setPadding(6, 2) // was 6,6,
      projectPane.packStart(navLabel, false, false, 0) // was 6
      projectPane.packStart(mainScrollpane, true, true, 0)

    }

    override protected[gtkui] def widget: Widget = projectPane

  }

  object EmbeddedPngIcons {
    import javax.xml.bind.DatatypeConverter
    
    private val transparentBase64: String = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAAACXBI" +
      "WXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3gcHEzswzxzx6wAAABl0RVh0Q29tbWVudABDcmVhdGVk" +
      "IHdpdGggR0lNUFeBDhcAAAASSURBVDjLY2AYBaNgFIwCCAAABBAAAYU/qnIAAAAASUVORK5CYII="
      /** A 16x16 fully transparent PNG */
    lazy val Transparent = DatatypeConverter.parseBase64Binary(transparentBase64)

    
    
  }
}