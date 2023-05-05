package com.apptest.utils

import com.getnetpd.enums.client.GPDTransactionType
import com.getnetpd.models.client.GPDAddressModel
import com.getnetpd.models.client.GPDShippingModel
import com.getnetpd.models.qrcode.GPDPayments

class Helpers {

    companion object {
        fun strToInt(value : String) : Int? {
            return if(value == "")
                null
            else
                value.toInt()
        }

        fun checkInputIsNotEmpty(value : String) : Boolean {
            if(value.isBlank())
                return false
            return true
        }

        fun textToIntIfNotEmpty(value : String) : Int? {
            if(value.isBlank())
                return null
            return value.toInt()
        }

        fun parseShippings(value : String): List<GPDShippingModel>? {
            if (value == "")
                return null

            val objects: ArrayList<GPDShippingModel> = ArrayList()
            val content = value.split("\n")
            for (line in content) {
                val lineContent = line.trim().split(',')
                val address = GPDAddressModel(
                    lineContent[5].trim(),
                    lineContent[6].trim(),
                    lineContent[7].trim(),
                    lineContent[8].trim(),
                    lineContent[9].trim(),
                    lineContent[10].trim(),
                    lineContent[11].trim(),
                    lineContent[12].trim()
                )
                objects.add(
                    GPDShippingModel(
                        lineContent[0].trim(),
                        lineContent[1].trim(),
                        lineContent[2].trim(),
                        lineContent[3].trim(),
                        lineContent[4].trim().toInt(),
                        address
                    )
                )
            }

            if (objects.isEmpty())
                return null
            return objects
        }

        fun parsePayment(value : String): List<GPDPayments>? {
            if (value == "")
                return null

            val objects: ArrayList<GPDPayments> = ArrayList()
            val content = value.split("\n")
            for (line in content) {
                val lineContent = line.trim().split(',')
                objects.add(
                    GPDPayments(
                        lineContent[0].trim(),
                        lineContent[1].trim().toBoolean(),
                        GPDTransactionType.valueOf(lineContent[2].trim()),
                        lineContent[3].trim().toInt(),
                        lineContent[4].trim(),
                        lineContent[5].trim().toInt(),
                        lineContent[6].trim(),
                    )
                )
            }

            if (objects.isEmpty())
                return null
            return objects
        }
    }
}