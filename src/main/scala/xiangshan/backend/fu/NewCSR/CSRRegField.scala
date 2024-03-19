package xiangshan.backend.fu.NewCSR

import chisel3._
import chisel3.experimental.SourceInfo
import chisel3.util.Cat

import scala.language.experimental.macros


abstract class CSRBundle extends Bundle {
  val len: Int = 64

  override def do_asUInt(implicit sourceInfo: SourceInfo): UInt = {
    val fields = this.getFields
    var paddedFields: Seq[Data] = Seq()
    var lsb = len

    for (field <- fields) {
      val diffWidth = lsb - field.lsb - field.getWidth
      if (diffWidth > 0)
        paddedFields :+= 0.U((diffWidth).W)
      paddedFields :+= field
      lsb = field.lsb
    }

    if (fields.last.lsb > 0) {
      paddedFields :+= 0.U(fields.last.lsb.W)
    }

    Cat(paddedFields.map(x => x.asUInt))
  }

  def := (that: UInt): Unit = {
    val fields = this.getFields

    for (field <- fields) {
      field := field.factory.apply(that(field.lsb + field.getWidth - 1, field.lsb))
    }
  }

  /**
   * filtered read connection
   *
   * CSRBundle will be filtered by CSRFields' read filter function.
   */
  def :|= [T <: CSRBundle](that: T): Unit = {
    if (this.getClass != that.getClass) {
      throw MonoConnectException(s"Type miss match! (sink :|= source) " +
        s"sink type: ${this.getClass}, " +
        s"source type: ${that.getClass}")
    }

    for ((sink: CSREnumType, source: CSREnumType)  <- this.getFields.zip(that.getFields)) {
      sink := sink.factory.apply(sink.rfn(source.asUInt, Seq()))
    }
  }

  def getFields = this.getElements.map(_.asInstanceOf[CSREnumType])
}
