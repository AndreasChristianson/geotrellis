/*
 * Copyright (c) 2014 Azavea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.io.geotiff

import geotrellis.raster._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.vector.Extent
import geotrellis.proj4.CRS

import geotrellis.testkit._

import org.scalatest._

class GeoTiffWriterSpec extends FunSpec
    with Matchers
    with BeforeAndAfterAll
    with TestEngine
    with GeoTiffTestUtils {

  override def afterAll = purge

  private val testProj4String =
    "+proj=lcc +lat_0=33.750000000 +lon_0=-79.000000000 +lat_1=36.166666667 +lat_2=34.333333333 +x_0=609601.220 +y_0=0.000 +ellps=GRS80 +units=m"

  private val testProj4StringMap = proj4StringToMap(testProj4String)

  private val testCRS = CRS.fromString(testProj4String)

  describe ("writing GeoTiffs without errors and with correct tiles, crs and extent") {

    it ("should write floating point rasters correct") {
      val e = Extent(100.0, 400.0, 120.0, 420.0)
      val r = DoubleArrayTile(Array(11.0, 22.0, 33.0, 44.0), 2, 2)
      val crs = testCRS

      val path = "/tmp/float.tif"

      GeoTiffWriter.write(path, r, e, crs)

      addToPurge(path)

      val ifd = GeoTiffReader(path).read.imageDirectories.head

      val (raster, extent) = ifd.toRaster

      extent should equal (e)
      raster should equal (r)

      val proj4String = ifd.proj4String getOrElse fail

      val tiffProj4Map = proj4StringToMap(proj4String)

      compareValues(tiffProj4Map, testProj4StringMap, "proj", false)
      compareValues(tiffProj4Map, testProj4StringMap, "ellps", false)
      compareValues(tiffProj4Map, testProj4StringMap, "units", false)
      compareValues(tiffProj4Map, testProj4StringMap, "lat_0", true)
      compareValues(tiffProj4Map, testProj4StringMap, "lon_0", true)
      compareValues(tiffProj4Map, testProj4StringMap, "lat_1", true)
      compareValues(tiffProj4Map, testProj4StringMap, "lat_2", true)
      compareValues(tiffProj4Map, testProj4StringMap, "x_0", true)
      compareValues(tiffProj4Map, testProj4StringMap, "y_0", true)
    }

  }

}
