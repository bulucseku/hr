package com.sentrana.biq.core.physical.Sql.PostgreSQL

import com.sentrana.appshell.metadata._
import com.sentrana.biq.core.conceptual.Metadata
import com.sentrana.biq.core.physical.{ SqlDataWarehouse, Table }
import com.sentrana.biq.core.{ QueryGenerator, Report }
import com.sentrana.biq.metadata.Connection

import scala.language.implicitConversions
import scala.util.Try
import scala.xml.Node

/**
 * Created by szhao on 1/12/2015.
 */
case class PostgreSQLDataWarehouse(
    override val repositoryId:  String,
    override val tables:        Traversable[Table],
    override val configuration: Map[String, String],
    override val connection:    Connection
) extends SqlDataWarehouse(repositoryId, tables, configuration, connection) {

  override val sources = Nil

  def getQueryGenerator(report: Report): QueryGenerator = {
    new PostgreSQLQueryGenerator(this, report)
  }
}

object PostgreSQLDataWarehouse {

  def apply(metadata: Metadata, repositoryId: String, warehouseNode: Node): Try[PostgreSQLDataWarehouse] = {
    for {
      tables <- parseSeq[Table](warehouseNode \ "tables" \ "table")(Table.fromXml(metadata))
      connection <- parseSeq[Connection](warehouseNode \ "connection")
    } yield PostgreSQLDataWarehouse(
      repositoryId,
      tables,
      parseConfiguration(warehouseNode),
      connection.head
    )
  }

  private def parseConfiguration(node: Node): Map[String, String] = {
    if ((node \ "configuration").nonEmpty)
      (node \ "configuration").head.attributes.map(
        data => (data.key, data.value.text)
      ).toMap
    else
      Map()
  }
}
