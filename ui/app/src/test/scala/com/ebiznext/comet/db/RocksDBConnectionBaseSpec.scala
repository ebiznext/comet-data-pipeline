package com.ebiznext.comet.db
import java.nio.file.Files
import java.util.UUID

import org.scalatest._

import scala.collection.mutable

/**
 * Created by Mourad on 31/07/2018.
 */
trait RocksDBConnectionSpecUtils {
  class RocksDBConnectionLike
    extends RocksDBConnection(
      RocksDBConfig(Files.createTempDirectory("comet-test").toFile.getAbsolutePath + "/" + UUID.randomUUID())
    )

  class RocksDBConnectionMock extends RocksDBConnectionLike {

    override def close(): Unit = {
      // dummy close
    }
    override def read[V <: AnyRef](key: String)(implicit m: Manifest[V]): Option[V] =
      RocksDBConnectionMock.dbMocked.get(key).asInstanceOf[Option[V]]
    override def write[V <: AnyRef](key: String, value: V)(implicit m: Manifest[V]): Unit =
      RocksDBConnectionMock.dbMocked.put(key, value)
    override def delete(key: String): Unit = RocksDBConnectionMock.dbMocked.remove(key)
  }

  object RocksDBConnectionMock {

    val dbMocked: mutable.HashMap[String, AnyRef] = mutable.HashMap.empty
  }
}

trait RocksDBConnectionBaseSpec
    extends TestSuite
    with RocksDBConnectionSpecUtils
    with BeforeAndAfterAll
    with BeforeAndAfter {

  lazy val rocksdbConnection = new RocksDBConnectionLike()
  lazy val rockdb = rocksdbConnection.db
}

trait RocksDBConnectionMockBaseSpec
    extends TestSuite
    with Matchers
    with RocksDBConnectionSpecUtils
    with BeforeAndAfterAll
    with BeforeAndAfter {

  lazy val rocksdbConnection = new RocksDBConnectionMock()

}
