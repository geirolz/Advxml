package advxml.syntax

import advxml.core
import advxml.core.{Converter, PureConverter, ValidatedConverter, ValidatedNelEx}
import cats.{Applicative, Id, Monad}
import cats.implicits._

private[syntax] trait ConvertersSyntax {

  implicit class ApplicativeConverterOps[F[_]: Applicative, A](fa: F[A]) {
    def mapAs[G[_], B](implicit s: Converter[G, A, B]): F[G[B]] = fa.map(Converter[G, A, B].run(_))
    def mapAs[B](implicit s: PureConverter[A, B], i: DummyImplicit): F[B] = fa.mapAs[Id, B]
  }

  implicit class MonadConverterOps[F[_]: Monad, A](fa: F[A]) {
    def flatMapAs[B](implicit s: Converter[F, A, B]): F[B] = fa.flatMap(Converter[F, A, B].run(_))
  }

  implicit class AnyConvertersOps[A](a: A) {

    /** Convert [[A]] into [[B]] using implicit [[Converter]] if available
      * and if it conforms to required types [[F]], [[A]] and [[B]].
      *
      * @see [[Converter]] for further information.
      */
    def asF[F[_], B](implicit F: Converter[F, A, B]): F[B] =
      Converter[F, A, B].run(a)

    /** Convert [[A]] into [[B]] using implicit [[core.PureConverter]] if available
      * and if it conforms to required types [[A]] and [[B]].
      *
      * @see [[core.PureConverter]] for further information.
      */
    def asPure[B](implicit F: PureConverter[A, B]): B =
      PureConverter[A, B].run(a)

    /** Convert [[A]] into [[B]] using implicit [[core.ValidatedConverter]] if available
      * and if it conforms to required types [[A]] and [[B]].
      *
      * @see [[Converter]] for further information.
      */
    def asValidated[B](implicit F: ValidatedConverter[A, B]): ValidatedNelEx[B] =
      ValidatedConverter[A, B].run(a)

    //************************************** ALIASES **************************************
    /** Alias to [[AnyConvertersOps.asF]]
      */
    def as[F[_], B](implicit F: Converter[F, A, B]): F[B] =
      asF[F, B]

    /** Alias to [[AnyConvertersOps.asPure]]
      */
    def as[B](implicit F: PureConverter[A, B], i1: DummyImplicit): B =
      asPure[B]

    /** Alias to [[AnyConvertersOps.asValidated]]
      */
    def as[B](implicit F: ValidatedConverter[A, B], i1: DummyImplicit, i2: DummyImplicit): ValidatedNelEx[B] =
      asValidated[B]
  }
}
