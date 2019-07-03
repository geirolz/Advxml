package com.github.geirolz.advxml.transform

import cats.MonadError

import scala.xml.NodeSeq

/**
  * Advxml
  * Created by geirolad on 26/06/2019.
  *
  * @author geirolad
  */
package object actions {

  type MonadEx[F[_]] = MonadError[F, Throwable]
  type XmlPredicate = NodeSeq => Boolean
  type XmlZoom = NodeSeq => NodeSeq

  object XmlPredicate {
    def apply(f: NodeSeq => Boolean): XmlPredicate = f(_)
  }

  object XmlZoom{
    def apply(f: NodeSeq => NodeSeq): XmlZoom = f(_)
  }
}
